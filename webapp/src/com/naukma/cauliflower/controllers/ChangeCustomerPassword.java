package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.Cryptographer;
import com.naukma.cauliflower.mail.EmailSender;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Артем on 02.12.2014.
 */
@WebServlet(name = "ChangeCustomerPassword")
public class ChangeCustomerPassword extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginController.class);
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathFrom  = request.getHeader("Referer");
        String userIdForNewPassAttribute = "userIdForNewPass";
        String newPasswordAttribute ="newPassword";

        User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if(us!= null && us.getUserRole().equals(UserRole.CUSTOMER.toString())){
            int userIdForNewPass = Integer.parseInt(request.getParameter(userIdForNewPassAttribute));
            String newPassword = request.getParameter(newPasswordAttribute);

            if (userIdForNewPass > 0) {
                if(newPassword.length()> 6) {
                    //hashing password
                    final String hashedPassword= Cryptographer.hmacSha1(newPassword);
                    logger.info(" reg controller :: hashed password form"+newPassword+" is "+hashedPassword);
                    //
                    User userForNewPass = null;
                    try {
                        userForNewPass = DAO.INSTANCE.changeUserPasswordById(userIdForNewPass, hashedPassword);
                    } catch (SQLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    if(userForNewPass!=null){
                        //create body
                        StringBuilder message= new StringBuilder();
                        message.append("<p>Your password has been changed!</p> <p style=\"text-transform:none;\">Your new password: <b>");
                        message.append(newPassword);
                        message.append("</b></p>");


                        String fullPath = getServletContext().getRealPath("/WEB-INF/mail/");
                        EmailSender.sendEmail(userForNewPass, EmailSender.CHANGE_PASSWORD, message.toString(), EmailSender.getTemplate("/mailTemplate.ftl", fullPath));
                        request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,CauliflowerInfo.OK_CHANGE_PASSWORD_MESSAGE);
                        response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_DASHBOARD_LINK);
                    }else{
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
                        response.sendRedirect(pathFrom);
                    }
                }else{
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PASSWORD_ERROR_MESSAGE);
                    response.sendRedirect(pathFrom);
                }
            }else{
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INCORRECT_USER_FOR_NEW_PASS_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
        }else{
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            response.sendRedirect(pathFrom);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
