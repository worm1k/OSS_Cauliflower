package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.GregorianCalendar;

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
        user = (User) request.getSession().getAttribute(CauliflowerInfo.userAttribute);
        String scenario = request.getParameter("scenario");
       // scenario = "NEW";
        if(user == null) {
            response.sendRedirect("auth.jsp");
        }

        try {
        if(scenario.equals(Scenario.NEW.toString()))
                scenarioNew(request);
        else if (scenario.equals(Scenario.DISCONNECT.toString()))
            scenarioDisconnect(request);
        else if (scenario.equals(Scenario.MODIFY.toString()))
                scenarioModify(request);
        }
        catch (SQLException e) {
            request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "System error, try again later, please");
         //   response.sendRedirect(pathFrom);
        }
        //ServletContext context = this.getServletContext();
        //RequestDispatcher dispatcher = context.getRequestDispatcher("/installationController");
        response.sendRedirect("dashboard.jsp");


        //for end2end
       // Task task = null;
       // try {
//            task = DAO.INSTANCE.getTaskById(taskId);
//        } catch (SQLException e) {
//            request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "System error, try again later, please");
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
        taskId = DAO.INSTANCE.createNewTask(orderId,UserRoles.INSTALLATION_ENG);
        //for end2end
      //  DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.PROCESSING);

    }

    private void scenarioModify(HttpServletRequest request) throws SQLException{
        Integer instanceId =  Integer.parseInt(request.getParameter("instanceId"));
        createModifyOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        DAO.INSTANCE.createNewTask(orderId,UserRoles.PROVISIONING_ENG);
    }

    private void scenarioDisconnect(HttpServletRequest request) throws SQLException
    {
        Integer instanceId =  Integer.parseInt(request.getParameter("instanceId"));
        createDisconectOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createNewTask(orderId,UserRoles.INSTALLATION_ENG);
//        //for end2end
//        try {
//            DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.PROCESSING);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    // ACK.1
    private void createNewOrder() throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.NEW,new GregorianCalendar(),null);
    }

    // ACK.3
    private void createDisconectOrder(Integer instanceId) throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.DISCONNECT,new GregorianCalendar(), instanceId);
    }

    private void createModifyOrder(Integer instanceId){
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.MODIFY,new GregorianCalendar(), instanceId);
    }


    //ACK 12
    private void changeOrderStatus() throws SQLException
    {
        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);
    }

    //ACK 12
    private void createServiceInstance(HttpServletRequest request)
    {
        ServiceLocation serviceLocation = (ServiceLocation)request.getSession().getAttribute(CauliflowerInfo.serviceLocationAttribute);
        Service service = (Service)request.getSession().getAttribute(CauliflowerInfo.serviceAttribute);
        serviceLocation.setServiceLocationId(DAO.INSTANCE.createServiceLocation(serviceLocation));
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation,service.getServiceId());
        request.getSession().removeAttribute(CauliflowerInfo.serviceAttribute);
        request.getSession().removeAttribute(CauliflowerInfo.serviceLocationAttribute);

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
