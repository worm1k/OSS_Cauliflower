package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.InstanceStatus;
import com.naukma.cauliflower.dao.OrderStatus;
import com.naukma.cauliflower.dao.Scenario;
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
    private int orderId = 0;
    private int serviceInstanceId = 0;
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
        Scenario scenario = (Scenario)request.getParameter("scenario");
        createNewOrder();
        changeOrderStatus();
        createServiceInstance();
        createTaskForInstallation();
        createTaskForProvisioning();



        // create new order
        //crate instance
        // check cable
        // create tasks
        // change tasks statuses
        // change instance status
        request.setAttribute("name", "value");
        request.getRequestDispatcher("index.jsp").forward(request, response);

    }

    // ACK.1
    private void createNewOrder()
    {
         orderId = DAO.INSTANCE.createServiceOrder(Scenario.NEW);


    }

    // ACK.3
    private void createDisconectOrder()
    {
        orderId = DAO.INSTANCE.createServiceOrder(Scenario.DISCONNECT);


    }

    //ACK 12
    private void changeOrderStatus(){
        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);


    }

    //ACK 12
    private void createServiceInstance()
    {
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(), 1, "home", 40, 40, 1);

    }

    private void createTaskForInstallation()
    {
        DAO.INSTANCE.createTask();

    }

    private void createTaskForProvisioning()
    {
        DAO.INSTANCE.createTask();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }


}
