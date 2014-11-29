package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.InstanceStatus;
import com.naukma.cauliflower.dao.OrderStatus;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Max on 29.11.2014.
 */
@WebServlet(name = "ProceedOrderController")
public class ProceedOrderController extends HttpServlet {
    private User user;
    private int orderId = -1;
    private int serviceInstanceId = -1;

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
        Scenario scenario = (Scenario)request.getSession().getAttribute("scenario");

        //   if(user != null) {
        if(scenario == Scenario.NEW)
            scenarioNew(request);
        else
            scenarioDisconnect(request);


//
//            createNewOrder();
//            changeOrderStatus();
//            createServiceInstance(request);
//            connectInstanceWithOrder();
//            if(scenario == Scenario.NEW)
//            DAO.INSTANCE.createTaskForInstallation(orderId);
//            else
//            DAO.INSTANCE.createTaskForProvisioning(orderId);


        // create new order
        //crate instance
        // check cable
        // create tasks
        // change tasks statuses
        // change instance status
    //    request.setAttribute("name", "value");
    //   request.getRequestDispatcher("index.jsp").forward(request, response);

    }

    private void scenarioNew(HttpServletRequest request)
    {
        createNewOrder();
        changeOrderStatus();
        createServiceInstance(request);
        connectInstanceWithOrder();
        setInstanceBlocked();
        DAO.INSTANCE.createTaskForInstallation(orderId);
    }

    private void scenarioDisconnect(HttpServletRequest request)
    {
        Integer instanceId =  Integer.parseInt(request.getParameter("instanceId"));
        createDisconectOrder(instanceId);
        changeOrderStatus();
        setInstanceBlocked();
        DAO.INSTANCE.createTaskForProvisioning(orderId);
    }

    // ACK.1
    private void createNewOrder()
    {
         orderId = DAO.INSTANCE.createServiceOrder(Scenario.NEW,null);
    }

    // ACK.3
    private void createDisconectOrder(Integer instanceId)
    {
        orderId = DAO.INSTANCE.createServiceOrder(Scenario.DISCONNECT, instanceId);
    }

    //ACK 12
    private void changeOrderStatus()
    {
        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);
    }

    //ACK 12
    private void createServiceInstance(HttpServletRequest request)
    {
        ServiceLocation serviceLocation = (ServiceLocation)request.getSession().getAttribute("serviceLocation");
        Service service = (Service)request.getSession().getAttribute("service");
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation, service.getServiceId());
    }

    private void connectInstanceWithOrder()
    {
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
    }

    private void setInstanceBlocked()
    {
        DAO.INSTANCE.setInstanceBlocked(serviceInstanceId);

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        
    }


}
