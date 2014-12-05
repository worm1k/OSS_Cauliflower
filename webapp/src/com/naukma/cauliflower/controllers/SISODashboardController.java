package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRoles;
import com.naukma.cauliflower.entities.ServiceInstance;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
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

      final String pathFrom  = request.getHeader("Referer");

        ArrayList<ServiceOrder> orders = null;
        ArrayList<ServiceInstance> instances = null;
        User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(user == null){
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }

        String role = user.getUserRole();
        try {
            if(role.equals(UserRoles.CUSTOMER.toString())){
                    orders = DAO.INSTANCE.getOrders(user.getUserId());
                instances = DAO.INSTANCE.getInstances(user.getUserId());
            }
            else if(role.equals(UserRoles.CUST_SUP_ENG.toString())){
                orders = DAO.INSTANCE.getAllOrders();
                instances = DAO.INSTANCE.getAllInstances();

            }

        } catch (SQLException e) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
            response.sendRedirect(pathFrom);
        }

        request.setAttribute(CauliflowerInfo.ORDERS_ATTRIBUTE, orders);
        request.setAttribute(CauliflowerInfo.INSTANCES_ATTRIBUTE, instances);
        request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK).forward(request, response);
        // JSP!!!!!!!
        ///<% List<ItemObj> myList = (ArrayList<ItemObj>) request.getParameter("list"); %>
    }
}
