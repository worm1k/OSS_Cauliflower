package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.EmailSender;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
 * This servlet controls blocking accounts of users by administrator.
 */
@WebServlet(name = "BlockAccountController")
public class BlockAccountController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Name of parameter that is taken from form in jsp
        String emailParameter = "email";

        //Checking for user in session and for his permissions for blocking accounts
        User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(us != null && us.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
            //Getting e-mail of account for block
            String userEmailForBlock = request.getParameter(emailParameter);
            try {
                //Checking for existing account for block
                if (DAO.INSTANCE.checkForExistingUserByEmail(userEmailForBlock)) {
                    //Blocking account
                    User blockedUser = DAO.INSTANCE.blockUserByEmail(userEmailForBlock);
                    //Sending e-mail to blocked account
                    String fullPath = getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH);
                    EmailSender.notifyUserAboutBlockedAccount(blockedUser, fullPath);
                    //Insertion attribute of successful blocking into session
                    request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,CauliflowerInfo.OK_ACCOUNT_BLOCK_MESSAGE);
                    if(blockedUser.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
                        //If administrator has blocked himself, do logout
                        ServletContext context = getServletContext();
                        RequestDispatcher rd = context.getRequestDispatcher(CauliflowerInfo.LOGOUT_CONTROLLER_LINK);
                        rd.forward(request, response);
                    }
                    //Redirect to administrator dashboard
                    else response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                } else {
                    //Insertion attribute of existing e-mail for block error into session
                    // and redirect to administrator dashboard
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                            CauliflowerInfo.INCORRECT_USER_FOR_BLOCK_ERROR_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //Insertion attribute of permission error into session and redirect to home page
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}