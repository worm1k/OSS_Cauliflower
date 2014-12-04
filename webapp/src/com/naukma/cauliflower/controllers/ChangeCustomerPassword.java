package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
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
        User us = (User)request.getSession().getAttribute(CauliflowerInfo.userAttribute);
        if(us!= null && us.getUserRole().trim().toLowerCase().equals("customersupportengineer")) {
            int userIdForNewPass = Integer.parseInt(request.getParameter("userIdForNewPass"));
            String newPassword = request.getParameter("newPassword");
            if (userIdForNewPass > 0) {
                if(newPassword.length()>6) {
                    User userForNewPass = DAO.INSTANCE.changeUserPasswordById(userIdForNewPass, newPassword);
                    if(userForNewPass!=null){

                        //create body
                        StringBuffer message= new StringBuffer();
                        message.append("<p>Your password has been changed!</p> <p style=\"text-transform:none;\">Your new password: <b>");
                        message.append(newPassword);
                        message.append("</b></p>");


                        String fullPath = getServletContext().getRealPath("/WEB-INF/mail/");
                        EmailSender.sendEmail(userForNewPass, EmailSender.CHANGE_PASSWORD, message.toString(), EmailSender.getTemplate("/mailTemplate.ftl", fullPath));
                        //OK
                        //redirect to customer support engineer dashboard
                    }else{
                        request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "System error, try again later, please");
                        response.sendRedirect(pathFrom);
                    }
                }else{
                    request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "Incorrect new password");
                    response.sendRedirect(pathFrom);
                }
            }else{
                request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "Incorrect user for change his password");
                response.sendRedirect(pathFrom);
            }
        }else{
            request.getSession().setAttribute(CauliflowerInfo.errorAttribute, "You don`t have permission");
            response.sendRedirect(pathFrom);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
