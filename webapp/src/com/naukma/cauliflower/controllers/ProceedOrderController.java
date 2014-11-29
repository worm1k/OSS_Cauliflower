package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
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
        createNewOrder();
        response.sendRedirect("index.jsp");


        // create new order
        //crate instance
        // check cable
        // create tasks
        // change tasks ctatuses
        // change instance status


    }

    // ACK.1
    private void createNewOrder()
    {
       orderId = DAO.INSTANCE.createServiceOrder("Entering","New");
        createServiceInstance();
        createTaskForInstallation();

    }

    // ACK.3
    private void createDisconectOrder()
    {
        orderId = DAO.INSTANCE.createServiceOrder("Entering","Disconnect");


    }

    private void createServiceInstance()
    {
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),1,"home",40,40,1,"Planned");



    }

    private void createTaskForInstallation()
    {


    }

    private void createTaskForProvisioning()
    {


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }


}
