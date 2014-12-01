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
 * Created by Артем on 01.12.2014.
 */
@WebServlet(name = "RegistrationController")
public class RegistrationController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email="";
        String password="";
        String firstName="";
        String lastName="";
        String phone="";
        String userRole="CUSTOMER";
        int userRoleId=1;

        email = (String) request.getParameter("email");
        password = (String) request.getParameter("password");
        firstName = (String) request.getParameter("name");
        lastName = (String) request.getParameter("surname");
        phone = (String) request.getParameter("phone");

        /*response.getWriter().println(email);
        response.getWriter().println(password);
        response.getWriter().println(firstName);
        response.getWriter().println(lastName);
        response.getWriter().println(phone);*/

        if(DAO.INSTANCE.checkForEmailUniq(email)){
            User user = new User(userRoleId,userRole,email,firstName,lastName,phone);
            int res = DAO.INSTANCE.createUser(user,password);
            if(res>0){
                //redirect
            }else{
                //response.set
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
