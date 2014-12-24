package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.EmailSender;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This servlet proceeds orders for three different scenarios
 * and starts Service Order Workflow according to scenario type
 *
 * For scenario NEW it creates order with name CREATE_CIRCUIT,creates service location,
 * creates instance with status "Planned" and blocks it,
 * connects order and instance,
 * creates task for installation engineer and send email to installation engineer user group
 *
 *  For scenario MODIFY it creates order with name MODIFY_SERVICE, blocks instance,
 * creates task for provisioning engineer,
 * connects task with new service and send email to provisioning engineer user group
 *
 * For scenario DISCONNECT it creates order with name BREAK_CIRCUIT, blocks instance,
 * connects instance, creates task for installation engineer and send email to installation engineer user group
 * 
 * */

 @WebServlet(name = "ProceedOrderController")
public class ProceedOrderController extends HttpServlet
{
    private User user = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String scenario = request.getParameter(CauliflowerInfo.SCENARIO_PARAM);
        if(user == null || user.isBlocked())
        {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
            return;
        }
        if(!user.getUserRole().equals(UserRole.CUSTOMER.toString()))
        {
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            return;
        }
        try {
            if (scenario==null || scenario.equals(Scenario.NEW.toString()))
            {
                 startWorkflowForScenarioNew(request);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            } else if (scenario.equals(Scenario.DISCONNECT.toString()))
            {
                startWorkflowForScenarioDisconnect(request);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            } else if (scenario.equals(Scenario.MODIFY.toString()))
            {
                startWorkflowForScenarioModify(request);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            }
        }
        catch (SQLException e)
        {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                    CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
        }

        return;

    }



    /**
     * Starts SO workflow for "NEW" scenario
     * @param request request for getting parameters
     * @throws java.sql.SQLException
     * */
    private void startWorkflowForScenarioNew(HttpServletRequest request) throws SQLException
    {
        Integer serviceInstance = null;
        int orderId = createServiceOrder(Scenario.NEW, serviceInstance);
        DAO.INSTANCE.changeOrderStatus(orderId, OrderStatus.PROCESSING);
        int serviceInstanceId = createServiceInstance(request.getSession());
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
        toggleInstanceBlocked(serviceInstanceId);
        DAO.INSTANCE.createNewTask(orderId,UserRole.
                INSTALLATION_ENG,
                TaskName.CREATE_CIRCUIT,
                TaskStatus.FREE);
        sendEmailNotificationForUserGroup(UserRole.INSTALLATION_ENG, TaskName.CREATE_CIRCUIT);
    }



    /**
     * Starts SO workflow for "MODIFY" scenario
     * @param request request for getting parameters
     * @throws java.sql.SQLException
     * */
    private void startWorkflowForScenarioModify(HttpServletRequest request) throws SQLException
    {
        int serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        if(!isInstanceBlockedOrDisconnected(serviceInstanceId,request)){
            int orderId = createServiceOrder(Scenario.MODIFY, serviceInstanceId);
            DAO.INSTANCE.changeOrderStatus(orderId, OrderStatus.PROCESSING);
            toggleInstanceBlocked(serviceInstanceId);
            int taskId = DAO.INSTANCE.createNewTask(orderId,
                    UserRole.PROVISIONING_ENG,
                    TaskName.MODIFY_SERVICE,
                    TaskStatus.FREE);
            int serviceId = Integer.parseInt(request.getParameter(CauliflowerInfo.SERVICE_ID_PARAM));
            DAO.INSTANCE.setServiceForTask(taskId, serviceId);
            sendEmailNotificationForUserGroup(UserRole.PROVISIONING_ENG, TaskName.MODIFY_SERVICE);

        }

    }


    /**
     * Starts SO workflow for "DISCONNECT" scenario
     * @param request request for getting parameters
     * @throws java.sql.SQLException
     * */
    private void startWorkflowForScenarioDisconnect(HttpServletRequest request) throws SQLException
    {
        int serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        if(!isInstanceBlockedOrDisconnected(serviceInstanceId,request)){
            int orderId = createServiceOrder(Scenario.DISCONNECT,serviceInstanceId);
            DAO.INSTANCE.changeOrderStatus(orderId, OrderStatus.PROCESSING);
            toggleInstanceBlocked(serviceInstanceId);
            DAO.INSTANCE.createNewTask(orderId,
                    UserRole.INSTALLATION_ENG,
                    TaskName.BREAK_CIRCUIT,
                    TaskStatus.FREE);
            sendEmailNotificationForUserGroup(UserRole.INSTALLATION_ENG,
                    TaskName.BREAK_CIRCUIT);

        }
    }

    /**
     * Checks if service instance blocked or disconnected
     * @param serviceInstanceId id of serviceInstance to check
     * @param request request to set error message
     * @throws java.sql.SQLException
     * @return true if instance blocked or disconnected, otherwise - false
     * */
    private boolean isInstanceBlockedOrDisconnected(int serviceInstanceId,HttpServletRequest request) throws SQLException{
        boolean blocked = DAO.INSTANCE.isInstanceBlocked(serviceInstanceId);
        boolean disconnected = DAO.INSTANCE.isInstanceDisconnected(serviceInstanceId);
        if(blocked)
        {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                    CauliflowerInfo.INSTANCE_IS_BLOCKED_ERROR_MESSAGE);

        }
        else if(disconnected)
        {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                    CauliflowerInfo.INSTANCE_IS_DISCONNECTED_ERROR_MESSAGE);
        }

        return blocked || disconnected;
    }


    /**
     * Creates new serviceOrder according to
     * scenario
     * @param serviceInstanceId id of the serviceInstance to connect with order , set null for scenario "NEW"
     * @param scenario workflow scenario of the service order
     * @throws java.sql.SQLException
     * @return id of the created service order
     * @see com.naukma.cauliflower.dao.Scenario
     * */
    private int createServiceOrder(Scenario scenario,Integer serviceInstanceId)
            throws SQLException
    {
        return  DAO.INSTANCE.createServiceOrder(user.getUserId(),scenario,serviceInstanceId);

    }

    /**
     * Creates new Service Instance and new Service Location of this service instance
     *@param session session to get service location and service attributes
     *@throws java.sql.SQLException
     * */
    private int createServiceInstance(HttpSession session) throws SQLException {
        ServiceLocation serviceLocation = (ServiceLocation)session.getAttribute(
                CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        Service service = (Service)session.getAttribute(
                CauliflowerInfo.SERVICE_ATTRIBUTE);
        int  serviceLocationId = DAO.INSTANCE.createServiceLocation(serviceLocation);
        serviceLocation.setServiceLocationId(serviceLocationId);
        int serviceInstanceId = DAO.INSTANCE.createServiceInstance(
                user.getUserId(),
                serviceLocation,
                service.getServiceId());
        session.removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        session.removeAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        return serviceInstanceId;
    }

    /**
     * Set instance status blocked for selected instance
     * @param serviceInstanceId id of the instance
     * @throws java.sql.SQLException
     * */
    private void toggleInstanceBlocked(int serviceInstanceId) throws SQLException
    {
        int trueAnswer = 1;
        DAO.INSTANCE.setInstanceBlocked(serviceInstanceId,trueAnswer);
    }

    /**
     * Send email notification about new task for selected user group
     * @param taskName name of the new task
     * @param userRole user group name
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskName
     * @see com.naukma.cauliflower.dao.UserRole
     * */
    private void sendEmailNotificationForUserGroup(UserRole userRole, TaskName taskName) throws SQLException{
        List<User> usersByUserRole = DAO.INSTANCE.getUsersByUserRole(userRole);
        EmailSender.sendEmailToGroup(usersByUserRole,taskName.toString(),
                getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH));

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}
