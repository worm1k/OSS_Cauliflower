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

        email = (String) request.getAttribute("email");
        password = (String) request.getAttribute("password");
        firstName = (String) request.getAttribute("firstName");
        lastName = (String) request.getAttribute("lastName");
        phone = (String) request.getAttribute("phone");

        if(DAO.INSTANCE.checkForEmailUniq(email)){
            User user = new User(-1,userRoleId,userRole,email,firstName,lastName,phone);
            int res = DAO.INSTANCE.createUser(user,password);
            if(res>0){
                //redirect
            }else{
                //redirect
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
