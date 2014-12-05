package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.ServiceInstance;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ihor on 03.12.2014.
 */
public class ReviewUserServiceInstance extends HttpServlet {
    private final static String USER_ACCOUNT="";
    //todo
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        ArrayList<ServiceInstance> userInstances = null;
        try {
            userInstances = DAO.INSTANCE.getInstances(user.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("userInstances",userInstances);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(USER_ACCOUNT);
        requestDispatcher.forward(req,resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                               doGet(req,resp);
    }
}
