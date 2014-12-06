package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRoles;
import com.naukma.cauliflower.entities.*;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@WebServlet(name = "SISODashboardController")
public class SISODashboardController  extends HttpServlet {


    /*
    ACK.14
    ACK.15
    */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        final String pathFrom  = request.getHeader("Referer");

        ArrayList<ServiceOrder> orders = null;
        ArrayList<ServiceInstance> instances = null;
        List<Service> lstService = null;
        User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter out = response.getWriter();
        if(user == null){
//            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
            mapper.writeValue(out, null);
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

            lstService = DAO.INSTANCE.getServices();

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(CauliflowerInfo.INSTANCES_ATTRIBUTE, instances);
            map.put(CauliflowerInfo.ORDERS_ATTRIBUTE, orders);
            map.put(CauliflowerInfo.SERVICE_ATTRIBUTE, lstService);
            mapper.writeValue(out, map);
        } catch (SQLException e) {
            e.printStackTrace();
//            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
//            response.sendRedirect(pathFrom);
            mapper.writeValue(out, null);
        }

//        request.setAttribute(CauliflowerInfo.ORDERS_ATTRIBUTE, orders);
//        request.setAttribute(CauliflowerInfo.INSTANCES_ATTRIBUTE, instances);
//        request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK).forward(request, response);
        // JSP!!!!!!!
        ///<% List<ItemObj> myList = (ArrayList<ItemObj>) request.getParameter("list"); %>
    }
}
