package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.Cryptographer;
import com.naukma.cauliflower.mail.EmailSender;

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
        User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(us!= null && us.getUserRole().trim().toLowerCase().equals("customersupportengineer")) {
            int userIdForNewPass = Integer.parseInt(request.getParameter("userIdForNewPass"));
            String newPassword = request.getParameter("newPassword");
            if (userIdForNewPass > 0) {
                if(newPassword.length()>6) {
                    //hashing password
                    final String hashPassword= Cryptographer.hmacSha1(newPassword);
                    //
                    User userForNewPass = DAO.INSTANCE.changeUserPasswordById(userIdForNewPass, hashPassword);
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
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
                        response.sendRedirect(pathFrom);
                    }
                }else{
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, "Incorrect new password");
                    response.sendRedirect(pathFrom);
                }
            }else{
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, "Incorrect user for change his password");
                response.sendRedirect(pathFrom);
            }
        }else{
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, "You don`t have permission");
            response.sendRedirect(pathFrom);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
