package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.mail.EmailSender;

import javax.servlet.ServletContext;
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
        String pathFrom  = request.getHeader("Referer");
        String email;
        String password;
        String firstName;
        String lastName;
        String phone;
        String userRole;
        int userRoleId;

        userRoleId = Integer.parseInt(request.getParameter("userRoleId"));
        if(userRoleId<=0){
            request.getSession().setAttribute("error", "Incorrect user role");
            response.sendRedirect(pathFrom);
        }
        userRole = DAO.INSTANCE.getUserRoleNameByUserRoleId(userRoleId);
        if(userRole==null){
            request.getSession().setAttribute("error", "Incorrect user role");
            response.sendRedirect(pathFrom);
        }
        email = request.getParameter("email");
        if(email==null || email.length()<6){
            request.getSession().setAttribute("error", "Incorrect e-mail");
            response.sendRedirect(pathFrom);
        }
        password = (String) request.getParameter("password");
        if(password==null || password.length()<6){
            request.getSession().setAttribute("error","Incorrect password");
            response.sendRedirect(pathFrom);
        }
        firstName = (String) request.getParameter("name");
        if(firstName==null || firstName.length()<2){
            request.getSession().setAttribute("error","Incorrect name");
            response.sendRedirect(pathFrom);
        }
        lastName = (String) request.getParameter("surname");
        if(lastName==null || lastName.length()<2){
            request.getSession().setAttribute("error","Incorrect surname");
            response.sendRedirect(pathFrom);
        }
        phone = (String) request.getParameter("phone");

        if(DAO.INSTANCE.checkForEmailUniq(email)){
            User user = new User(userRoleId,userRole,email,firstName,lastName,phone);
            int resId = DAO.INSTANCE.createUser(user,password);
            if(resId>0){
                user = new User(resId,userRoleId,userRole,email,firstName,lastName,phone);
                request.getSession().setAttribute("user",user);
                String fullPath = getServletContext().getRealPath("/WEB-INF/mail/");
                EmailSender.sendEmail(user, EmailSender.SUBJECT_REGISTRATION, password, EmailSender.getTemplate("/regTemplate.ftl", fullPath));
                //redirect to dashboard
                response.getWriter().println("new user: ");
                response.getWriter().println(user);
                response.getWriter().println(pathFrom);
                //check for previos page, redirct to max`s servlet or to user dashboard
                //response.sendRedirect(pathFrom);
            }else{
                request.getSession().setAttribute("error","System error, try again later, please");
                response.sendRedirect(pathFrom);
            }
        }else{
            request.getSession().setAttribute("error","User with this e-mail already exist");
            response.sendRedirect(pathFrom);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
