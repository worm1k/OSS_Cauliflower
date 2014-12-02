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
 * Created by Артем on 02.12.2014.
 */
@WebServlet(name = "ChangeCustomerPassword")
public class ChangeCustomerPassword extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathFrom  = request.getHeader("Referer");
        User us = (User)request.getSession().getAttribute("user");
        if(us.getUserRole().trim().toLowerCase().equals("customersupportengineer")) {
            int userIdForNewPass = Integer.parseInt(request.getParameter("userIdForNewPass"));
            String newPassword = request.getParameter("newPassword");
            if (userIdForNewPass > 0) {
                if(newPassword.length()>6) {
                    User userForNewPass = DAO.INSTANCE.changeUserPasswordById(userIdForNewPass, newPassword);
                    if(userForNewPass!=null){
                        String message = "Your password has been changed!\nYour new password: "+newPassword;
                        ServletContext context = getServletContext();
                        String fullPath = context.getRealPath("/WEB-INF/mail/");
                        EmailSender.sendEmail(userForNewPass, "CauliFlower", message, EmailSender.getTemplate("/html-mail-template.ftl", fullPath));
                        //OK
                        //redirect to customer support engineer dashboard
                    }else{
                        request.getSession().setAttribute("error", "System error, try again later, please");
                        response.sendRedirect(pathFrom);
                    }
                }else{
                    request.getSession().setAttribute("error", "Incorrect new password");
                    response.sendRedirect(pathFrom);
                }
            }else{
                request.getSession().setAttribute("error", "Incorrect user for change his password");
                response.sendRedirect(pathFrom);
            }
        }else{
            request.getSession().setAttribute("error", "You don`t have permission");
            response.sendRedirect(pathFrom);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
