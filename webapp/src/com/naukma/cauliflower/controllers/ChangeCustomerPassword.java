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
 *
 * This servlet controls changing password of users by customer support engineer.
 */
@WebServlet(name = "ChangeCustomerPassword")
public class ChangeCustomerPassword extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Parameters for validation new password and account for changing password
        int minPasswordLength = 6;
        int minUserId = 1;

        //Names of parameters that are taken from form in jsp
        String userIdForNewPassParameter = "userIdForNewPass";
        String newPasswordParameter = "newPassword";

        //Checking for user in session and for his permissions for changing password
        User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(us != null && us.getUserRole().equals(UserRole.CUST_SUP_ENG.toString())){
            //Getting parameters for changing password
            int userIdForNewPass = Integer.parseInt(request.getParameter(userIdForNewPassParameter));
            String newPassword = request.getParameter(newPasswordParameter);
            //Validation account for changing password and new password
             if (userIdForNewPass >= minUserId) {
                if(newPassword.length() >= minPasswordLength) {
                    //Hashihg new password
                    final String hashedPassword = Cryptographer.hmacSha1(newPassword);
                    logger.info(" reg controller :: hashed password form"+newPassword+" is "+hashedPassword);
                    User userForNewPass = null;
                    //Changing password
                    try {
                        userForNewPass = DAO.INSTANCE.changeUserPasswordById(userIdForNewPass, hashedPassword);
                    } catch (SQLException e) {
                        //Insertion attribute of system error into session and redirect to customer support engineer dashboard
                        logger.error(e);
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
                        response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_DASHBOARD_LINK);
                    }
                    //Sending e-mail to account with new password
                    String fullPath = getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH);
                    EmailSender.sendChangedPasswordToUser(userForNewPass,newPassword,fullPath);
                    //Insertion attribute of successful changing password into session
                    // and redirect to customer support engineer dashboard
                    request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,
                            CauliflowerInfo.OK_CHANGE_PASSWORD_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_USER_INFORMATION_LINK);
                }else{
                    //Insertion attribute of incorrect new password error into session
                    // and redirect to customer support engineer dashboard
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                            CauliflowerInfo.PASSWORD_ERROR_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_DASHBOARD_LINK);
                }
            }else{
                 //Insertion attribute of incorrect account for changing password error into session
                 // and redirect to customer support engineer dashboard
                 request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                         CauliflowerInfo.INCORRECT_USER_FOR_NEW_PASS_ERROR_MESSAGE);
                 response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_DASHBOARD_LINK);
            }
        }else{
            //Insertion attribute of permission error into session and redirect to home page
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}
