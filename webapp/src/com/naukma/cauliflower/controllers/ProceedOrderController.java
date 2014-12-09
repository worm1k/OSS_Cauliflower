package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Max on 29.11.2014.
 */
@WebServlet(name = "ProceedOrderController")
public class ProceedOrderController extends HttpServlet {
    private User user;
    private int orderId = -1;
    private int serviceInstanceId = -1;
    private int taskId = -1;

    /*
    ACK.1  ACK.2(OPTIONAL)
    ACK.3
    ACK.4
    ACK.10
    ACK.12
    SOW1
    SOW2
    SOW3
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       // System.out.println("IN PROCEED ORDER");
        user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String scenario = request.getParameter(CauliflowerInfo.SCENARIO_PARAM);
       // scenario = "NEW";
        if(user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }
        try {
        if(scenario==null || scenario.equals(Scenario.NEW.toString()))
                scenarioNew(request);
        else if (scenario.equals(Scenario.DISCONNECT.toString()))
            scenarioDisconnect(request,response);
        else if (scenario.equals(Scenario.MODIFY.toString()))
                scenarioModify(request,response);
        }
        catch (SQLException e) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
         //   response.sendRedirect(pathFrom);
        }
        //ServletContext context = this.getServletContext();
        //RequestDispatcher dispatcher = context.getRequestDispatcher("/installationController");
        response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);


        //for end2end
       // Task task = null;
       // try {
//            task = DAO.INSTANCE.getTaskById(taskId);
//        } catch (SQLException e) {
//            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, "System error, try again later, please");
//            response.sendRedirect(pathFrom);
//        }
//      //  request.setAttribute("task",task);
//        dispatcher.forward(request, response);

     //  request.getRequestDispatcher("dashboard.jsp").forward(request, response);

    }

    private void scenarioNew(HttpServletRequest request) throws SQLException {
        createNewOrder();
        changeOrderStatus();
        createServiceInstance(request);
        connectInstanceWithOrder();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.INSTALLATION_ENG,TaskName.CONNECT_NEW_PERSON);
        //for end2end
      //  DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.PROCESSING);

    }

    private void scenarioModify(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        Integer instanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        checkBlocked(instanceId,request,response);
        createModifyOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.PROVISIONING_ENG,TaskName.MODIFY_SERVICE);
        setNewServiceForTask(request);
    }

    private void scenarioDisconnect(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        Integer instanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        checkBlocked(instanceId,request,response);
        createDisconnectOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.INSTALLATION_ENG,TaskName.BREAK_CIRCUIT);
    }

    private void checkBlocked(int serviceInstanceId,HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {

        boolean blocked = DAO.INSTANCE.isInstanceBlocked(serviceInstanceId);
        if(blocked)
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_BLOCKED_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);

    }

    private void createNewOrder() throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.NEW,null);
    }

    private void createDisconnectOrder(Integer instanceId) throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.DISCONNECT, instanceId);
    }

    private void createModifyOrder(Integer instanceId)throws SQLException{
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.MODIFY, instanceId);
    }

    private void setNewServiceForTask(HttpServletRequest request) throws SQLException{
        Service service =(Service)request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        DAO.INSTANCE.setServiceForTask(taskId,service.getServiceId());

    }

    private void changeOrderStatus() throws SQLException
    {
        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);
    }

    private void createServiceInstance(HttpServletRequest request) throws SQLException {
        ServiceLocation serviceLocation = (ServiceLocation)request.getSession().getAttribute(
                CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        Service service = (Service)request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        serviceLocation.setServiceLocationId(DAO.INSTANCE.createServiceLocation(serviceLocation));
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation,service.getServiceId());
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);

    }


    private void connectInstanceWithOrder() throws SQLException
    {
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
    }

    private void setInstanceBlocked() throws SQLException
    {
            DAO.INSTANCE.setInstanceBlocked(serviceInstanceId,1);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       // doPost(request,response);
    }


}
