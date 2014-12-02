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
        String email;
        String password;
        String firstName;
        String lastName;
        String phone;
        String userRole="CUSTOMER";
        int userRoleId=1;

        email = (String) request.getParameter("email");
        if(email==null || email.length()<6){
            request.getSession().setAttribute("error", "Incorrect e-mail");
            response.sendRedirect("customreg.jsp");
        }
        password = (String) request.getParameter("password");
        if(password==null || password.length()<6){
            request.getSession().setAttribute("error","Incorrect password");
            response.sendRedirect("customreg.jsp");
        }
        firstName = (String) request.getParameter("name");
        if(firstName==null || firstName.length()<2){
            request.getSession().setAttribute("error","Incorrect name");
            response.sendRedirect("customreg.jsp");
        }
        lastName = (String) request.getParameter("surname");
        if(lastName==null || lastName.length()<2){
            request.getSession().setAttribute("error","Incorrect surname");
            response.sendRedirect("customreg.jsp");
        }
        phone = (String) request.getParameter("phone");

        if(DAO.INSTANCE.checkForEmailUniq(email)){
            User user = new User(userRoleId,userRole,email,firstName,lastName,phone);
            int resId = DAO.INSTANCE.createUser(user,password);
            if(resId>0){
                user = new User(resId,userRoleId,userRole,email,firstName,lastName,phone);
                request.getSession().setAttribute("user",user);
                String message = "Thank you for registration in CauliFlower\nYour password: "+password;
                ServletContext context = getServletContext();
                String fullPath = context.getRealPath("/WEB-INF/mail/");
                EmailSender.sendEmail(user, "Registration in CauliFlower", message, EmailSender.getTemplate("/html-mail-template.ftl", fullPath));
                //redirect to dashboard
                response.getWriter().println("new user: ");
                response.getWriter().println(user);
            }else{
                request.getSession().setAttribute("error","System error, try again later, please");
                response.sendRedirect("customreg.jsp");
            }
        }else{
            request.getSession().setAttribute("error","User with this e-mail already exist");
            response.sendRedirect("customreg.jsp");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
