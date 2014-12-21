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
 */
@WebServlet(name = "BlockAccountController")
public class BlockAccountController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailAttribute = "email";
        User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(us!=null && us.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
            String userEmailForBlock = request.getParameter(emailAttribute);
            try {
                if (DAO.INSTANCE.checkForExistingUserByEmail(userEmailForBlock)) {
                    User blockedUser = DAO.INSTANCE.blockUserByEmail(userEmailForBlock);
                    if (blockedUser != null) {
                        request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
                        request.getSession().removeAttribute(CauliflowerInfo.OK_ATTRIBUTE);
                        String fullPath = getServletContext().getRealPath("/WEB-INF/mail/");
                        EmailSender.notifyUserAboutBlockedAccount(blockedUser, fullPath);
                        request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,CauliflowerInfo.OK_ACCOUNT_BLOCK_MESSAGE);
                        if(blockedUser.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
                            ServletContext context = getServletContext();
                            RequestDispatcher rd = context.getRequestDispatcher(CauliflowerInfo.LOGOUT_CONTROLLER_LINK);
                            rd.forward(request, response);
                        }else response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                    } else {
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
                        response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                    }
                } else {
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INCORRECT_USER_FOR_BLOCK_ERROR_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}