package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.Cryptographer;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by Max on 26.11.2014.
 */
@WebServlet(name = "LoginController")
public class LoginController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info(" INFO ::   LoginController");

        String pathFrom  = request.getHeader("Referer");
        User userInSession = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(userInSession==null) {
            String username = request.getParameter("username").toLowerCase();
            String password = request.getParameter("password");
            //String res = "";
            Service service = (Service) request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
            ServiceLocation servLoc = (ServiceLocation) request.getSession().getAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);

            User user = null;
            try {
                //hashing password
                  String hashedPassword= Cryptographer.hmacSha1(password);
                logger.info("LoginController:: hashed password form"+password+" is "+hashedPassword);
                //
                user = DAO.INSTANCE.getUserByLoginAndPassword(username, hashedPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (user == null) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.LOGIN_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            } else {
                if(user.isBlocked()){
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.ACCOUNT_IS_BLOCKED_ERROR_MESSAGE);
                    response.sendRedirect(pathFrom);
                }else {
                    request.getSession().setAttribute(CauliflowerInfo.USER_ATTRIBUTE, user);
                    logger.info(" LOGGER ::   LoginController  : user is" + user.getFirstName());
                    request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
                    if (service != null && servLoc != null) {
                        ServletContext context = getServletContext();
                        RequestDispatcher rd = context.getRequestDispatcher("/proceed");
                        rd.forward(request, response);
                    } else {
                        int userInSessionRoleId = user.getUserRoleId();
                        if (userInSessionRoleId == CauliflowerInfo.CUSTOM_USER_ROLE_ID)
                            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                        if (userInSessionRoleId == CauliflowerInfo.ADMINISTRATOR_ROLE_ID)
                            response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                        if (userInSessionRoleId == CauliflowerInfo.PROVISIONING_ENG_ROLE_ID)
                            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                        if (userInSessionRoleId == CauliflowerInfo.INSTALLATION_ENG_ROLE_ID)
                            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                        if (userInSessionRoleId == CauliflowerInfo.CUST_SUP_ENG_ROLE_ID)
                            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                    }
                }
            }
        }else{
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            response.sendRedirect(pathFrom);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        final User user = (User)session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter out = response.getWriter();

        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(out, user);
    }
}