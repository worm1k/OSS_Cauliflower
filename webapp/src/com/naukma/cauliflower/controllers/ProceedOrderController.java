package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        user = (User) request.getSession().getAttribute("user");
        String scenario = request.getParameter("scenario");
        scenario = "NEW";
        //scenario = Scenario.NEW;
        //   if(user != null) {
        if(scenario.equals(Scenario.NEW.toString()))
            scenarioNew(request);
        else
            scenarioDisconnect(request);
    //    request.setAttribute("name", "value");
        ServletContext context = this.getServletContext();
        RequestDispatcher dispatcher = context.getRequestDispatcher("/");

      //for end2end
      //  Task tasl = DAO.INSTANCE.getTaskById(taskId)
       // request.setAttribute("task",);
        dispatcher.forward(request, response);

     //  request.getRequestDispatcher("dashboard.jsp").forward(request, response);

    }

    private void scenarioNew(HttpServletRequest request)
    {
        createNewOrder();
        changeOrderStatus();
        createServiceInstance(request);
        connectInstanceWithOrder();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createTaskForInstallation(orderId);
        //for end2end
        DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.PROCESSING);

    }

    private void scenarioDisconnect(HttpServletRequest request)
    {
        Integer instanceId =  Integer.parseInt(request.getParameter("instanceId"));
        createDisconectOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        taskId = DAO.INSTANCE.createTaskForInstallation(orderId);
        //for end2end
        DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.PROCESSING);
    }

    // ACK.1
    private void createNewOrder()
    {

        orderId = DAO.INSTANCE.createServiceOrder(Scenario.NEW,new GregorianCalendar(),null);
    }

    // ACK.3
    private void createDisconectOrder(Integer instanceId)
    {
        orderId = DAO.INSTANCE.createServiceOrder(Scenario.DISCONNECT,new GregorianCalendar(), instanceId);
    }


//    private GregorianCalendar getDate(){
//        GregorianCalendar calendar = new GregorianCalendar();
//        calendar.set(GregorianCalendar.YEAR,GregorianCalendar.MONTH,GregorianCalendar.DAY_OF_MONTH);
//        return calendar;
//    }


    //ACK 12
    private void changeOrderStatus()
    {

        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);
    }

    //ACK 12
    private void createServiceInstance(HttpServletRequest request)
    {

       // ServiceLocation serviceLocation = (ServiceLocation)request.getAttribute("serviceLocation");
                                            //(ServiceLocation)request.getSession().getAttribute("serviceLocation");
        ServiceLocation serviceLocation = new ServiceLocation(-1, "TRY ADRESS", 111, 999);
        Service service = (Service)request.getSession().getAttribute("service");
        serviceLocation.setServiceLocationId(DAO.INSTANCE.createServiceLocation(serviceLocation));
        //serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation, service.getServiceId());
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation, 1);
      //  request.getSession().removeAttribute("serviceLocation");
      //  request.getSession().removeAttribute("service");

    }


    private void connectInstanceWithOrder()
    {
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
    }

    private void setInstanceBlocked()
    {
        DAO.INSTANCE.setInstanceBlocked(serviceInstanceId,1);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }


}
