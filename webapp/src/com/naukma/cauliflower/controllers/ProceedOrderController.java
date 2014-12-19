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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet(name = "ProceedOrderController")
public class ProceedOrderController extends HttpServlet
{
    private User user = null;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String scenario = request.getParameter(CauliflowerInfo.SCENARIO_PARAM);
        if(user == null || user.isBlocked())
        {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }
        if(!user.getUserRole().equals(UserRole.CUSTOMER))
        {
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        }
        try
        {
            if(scenario==null || scenario.equals(Scenario.NEW.toString()))
            {
                scenarioNew(request);
                request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK);
            }
            else if (scenario.equals(Scenario.DISCONNECT.toString()))
            {
                scenarioDisconnect(request);
                request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK);
            }
            else if (scenario.equals(Scenario.MODIFY.toString()))
            {
                scenarioModify(request);
                request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK);
            }
        }
        catch (SQLException e)
        {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                    CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
        }

        return;

    }

    private void scenarioNew(HttpServletRequest request) throws SQLException
    {
        Integer serviceInstance = null;
        int orderId = createServiceOrder(Scenario.NEW, serviceInstance);
        changeOrderStatus(orderId,OrderStatus.PROCESSING);
        int serviceInstanceId = createServiceInstance(request);
        connectInstanceWithOrder(orderId,serviceInstanceId);
        toggleInstanceBlocked(serviceInstanceId);
        DAO.INSTANCE.createNewTask(orderId,UserRole.
                INSTALLATION_ENG,
                TaskName.CREATE_CIRCUIT,
                TaskStatus.FREE);
        sendEmailNotificationForUserGroup(UserRole.INSTALLATION_ENG, TaskName.CREATE_CIRCUIT);
    }

    private void scenarioModify(HttpServletRequest request) throws SQLException, IOException
    {
        int serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
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
        else
        {
            int orderId = createServiceOrder(Scenario.MODIFY, serviceInstanceId);
            changeOrderStatus(orderId,OrderStatus.PROCESSING);
            toggleInstanceBlocked(serviceInstanceId);
            int taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.PROVISIONING_ENG,TaskName.MODIFY_SERVICE,TaskStatus.FREE);
            int serviceId = Integer.parseInt(request.getParameter(CauliflowerInfo.SERVICE_ID_PARAM));
            setNewServiceForTask(taskId,serviceId);
            sendEmailNotificationForUserGroup(UserRole.PROVISIONING_ENG, TaskName.MODIFY_SERVICE);

        }

    }



    private void scenarioDisconnect(HttpServletRequest request) throws SQLException, IOException
    {
        int serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
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
        else
        {
            int orderId = createServiceOrder(Scenario.DISCONNECT,serviceInstanceId);
            changeOrderStatus(orderId,OrderStatus.PROCESSING);
            toggleInstanceBlocked(serviceInstanceId);
            DAO.INSTANCE.createNewTask(orderId,
                    UserRole.INSTALLATION_ENG,
                    TaskName.BREAK_CIRCUIT,
                    TaskStatus.FREE);
            sendEmailNotificationForUserGroup(UserRole.INSTALLATION_ENG,
                    TaskName.BREAK_CIRCUIT);

        }
    }

    private int createServiceOrder(Scenario scenario,Integer serviceInstanceId)
            throws SQLException
    {
        return  DAO.INSTANCE.createServiceOrder(user.getUserId(),scenario,serviceInstanceId);

    }

    private void setNewServiceForTask(int taskId, int serviceId) throws SQLException
    {
        DAO.INSTANCE.setServiceForTask(taskId,serviceId);


    }

    private void changeOrderStatus(int orderId,OrderStatus orderStatus) throws SQLException
    {
        DAO.INSTANCE.changeOrderStatus(orderId,orderStatus);
    }

    private int createServiceInstance(HttpServletRequest request) throws SQLException {
        ServiceLocation serviceLocation = (ServiceLocation)request.getSession().getAttribute(
                CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        Service service = (Service)request.getSession().getAttribute(
                CauliflowerInfo.SERVICE_ATTRIBUTE);
        int  serviceLocationId = DAO.INSTANCE.createServiceLocation(serviceLocation);
        serviceLocation.setServiceLocationId(serviceLocationId);
        int serviceInstanceId = DAO.INSTANCE.createServiceInstance(
                user.getUserId(),
                serviceLocation,service.getServiceId());
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        return serviceInstanceId;
    }


    private void connectInstanceWithOrder(int serviceInstanceId,int orderId) throws SQLException
    {
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
    }

    private void toggleInstanceBlocked(int serviceInstanceId) throws SQLException
    {
        int trueAnswer = 1;
        DAO.INSTANCE.setInstanceBlocked(serviceInstanceId,trueAnswer);
    }

    private void sendEmailNotificationForUserGroup(UserRole userRole, TaskName taskName) throws SQLException{
        List<User> usersByUserRole = DAO.INSTANCE.getUsersByUserRole(userRole);
        EmailSender.sendEmailToGroup(usersByUserRole,taskName.toString(),
                getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH));

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }


}
