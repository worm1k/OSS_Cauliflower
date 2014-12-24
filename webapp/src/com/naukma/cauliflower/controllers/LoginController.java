package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
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
 *
 * This servlet controls login of users.
 */
@WebServlet(name = "LoginController")
public class LoginController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info(" INFO ::   LoginController");
        //Names of parameters that are taken from form in jsp
        String usernameParameter = "username";
        String passwordParameter = "password";

        //Checking for user in session
        User userInSession = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(userInSession == null) {
            //Getting parameters for login
            String email = request.getParameter(usernameParameter).toLowerCase();
            String password = request.getParameter(passwordParameter);
            Service service = (Service) request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
            ServiceLocation servLoc = (ServiceLocation) request.getSession()
                    .getAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);

            User userForLogin = null;
            try {
                //Hashihg password
                String hashedPassword = Cryptographer.hmacSha1(password);
                logger.info("LoginController:: hashed password form"+password+" is "+hashedPassword);
                //Validation email and password and getting user from database
                userForLogin = DAO.INSTANCE.getUserByLoginAndPassword(email, hashedPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (userForLogin == null) {
                //Insertion attribute of login error into session and redirect to login page
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.LOGIN_ERROR_MESSAGE);
                response.sendRedirect(CauliflowerInfo.AUTH_LINK);
            } else {
                //Checking for blocking of user account
                if(userForLogin.isBlocked()){
                    //Insertion attribute of blocking account error into session and redirect to login page
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                            CauliflowerInfo.ACCOUNT_IS_BLOCKED_ERROR_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.AUTH_LINK);
                }else {
                    //Insertion user attribute into session
                    request.getSession().setAttribute(CauliflowerInfo.USER_ATTRIBUTE, userForLogin);
                    logger.info(" LOGGER ::   LoginController  : user is" + userForLogin.getFirstName());
                    //Checking for user selecting attributes to create a new connection
                    if (service != null && servLoc != null
                            && userForLogin.getUserRole().equals(UserRole.CUSTOMER.toString())) {
                        //Redirect to servlet of creating new connection
                        ServletContext context = getServletContext();
                        RequestDispatcher rd = context.getRequestDispatcher(CauliflowerInfo.PROCEED_CONTROLLER_LINK);
                        rd.forward(request, response);
                    } else {
                        //Redirection on the dashboard of the user who is logged
                        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
                        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
                        String userInSessionRole = userForLogin.getUserRole();
                        if (userInSessionRole.equals(UserRole.CUSTOMER.toString()))
                            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                        if (userInSessionRole.equals(UserRole.ADMINISTRATOR.toString()))
                            response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                        if (userInSessionRole.equals(UserRole.PROVISIONING_ENG.toString()))
                            response.sendRedirect(CauliflowerInfo.PROVIS_ENGINEER_DASHBOARD_LINK);
                        if (userInSessionRole.equals(UserRole.INSTALLATION_ENG.toString()))
                            response.sendRedirect(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK);
                        if (userInSessionRole.equals(UserRole.CUST_SUP_ENG.toString()))
                            response.sendRedirect(CauliflowerInfo.SUPPORT_ENGINEER_DASHBOARD_LINK);
                    }
                }
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