package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.ServiceInstance;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebServlet(name = "SISODashboardController")
public class SISODashboardController  extends HttpServlet {


    /*
    ACK.14
    ACK.15
    */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<ServiceOrder> orders = null;
        ArrayList<ServiceInstance> instances = null;
        User user = (User)request.getSession().getAttribute("user");
        String role = user.getUserRole();
        if(role.equals("CUSTOMER")){
            orders = DAO.INSTANCE.getOrders(user.getUserId());
            instances = DAO.INSTANCE.getInstances(user.getUserId());
        }
        else if(role.equals("CUST_SUP_ENG")){
            orders = DAO.INSTANCE.getAllOrders();
            instances = DAO.INSTANCE.getAllInstances();

        }
        request.setAttribute("orders", orders);
        request.setAttribute("instances", instances);
        request.getRequestDispatcher("dashboard.jsp").forward(request,response);
    }
}
