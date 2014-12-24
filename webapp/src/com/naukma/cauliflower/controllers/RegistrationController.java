package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.Cryptographer;
import com.naukma.cauliflower.mail.EmailSender;
import org.apache.log4j.Logger;

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
 * Created by Артем on 01.12.2014.
 *
 * This servlet controls registration of custom users by themselves and registration of engineers by administrator.
 */

@WebServlet(name = "RegistrationController")
public class RegistrationController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Names of parameters that are taken from form in jsp
        String userForRegisterRoleParameter = "userRole";
        String emailParameter = "email";
        String passwordParameter = "password";
        String firstNameParameter = "name";
        String lastNameParameter = "surname";
        String phoneParameter = "phone";

        //Getting role of user who will be registered
        String userRoleForRegister = request.getParameter(userForRegisterRoleParameter);
        if(userRoleForRegister == null) userRoleForRegister=UserRole.CUSTOMER.toString();
        //Checking for role of user who will register new user and for his permissions for register new user
        User userInSession = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if((userInSession == null && userRoleForRegister.equals(UserRole.CUSTOMER.toString())) ||
                (userInSession != null && userInSession.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                        && !userRoleForRegister.equals(UserRole.CUSTOMER.toString()))) {
            int userRoleId=0;
            //Getting parameters for registration of new user
            String email = request.getParameter(emailParameter).toLowerCase();
            String password = request.getParameter(passwordParameter);
            String firstName = request.getParameter(firstNameParameter);
            String lastName = request.getParameter(lastNameParameter);
            String phone = request.getParameter(phoneParameter);
            try {
                //Getting role id of user who will be registered
                if(userRoleForRegister.equals(UserRole.CUSTOMER.toString()))
                    userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.CUSTOMER);
                if(userRoleForRegister.equals(UserRole.PROVISIONING_ENG.toString()))
                    userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.PROVISIONING_ENG);
                if(userRoleForRegister.equals(UserRole.CUST_SUP_ENG.toString()))
                    userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.CUST_SUP_ENG);
                if(userRoleForRegister.equals(UserRole.INSTALLATION_ENG.toString()))
                    userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.INSTALLATION_ENG);
                //Check for uniqueness e-mail of user who will be registered
                if (DAO.INSTANCE.checkForEmailUniq(email)) {
                    //Check for uniqueness phone number of user who will be registered
                    if (DAO.INSTANCE.checkForPhoneUniq(phone)) {
                        User registeredUser = new User
                                (0, userRoleId, userRoleForRegister, email, firstName, lastName, phone, false);
                        //Hashihg password of user who will be registered
                        String hashedPassword = Cryptographer.hmacSha1(password);
                        logger.info(" reg controller :: hashed password form" + password + " is " + hashedPassword);
                        //Insertion new user into database and getting his id
                        int registeredUserId = DAO.INSTANCE.createUser(registeredUser, hashedPassword);
                        registeredUser = new User
                                (registeredUserId, userRoleId, userRoleForRegister, email, firstName, lastName, phone, false);
                        if (userInSession == null) {
                            //Insertion user attribute into session
                            request.getSession().setAttribute(CauliflowerInfo.USER_ATTRIBUTE, registeredUser);
                            userInSession = registeredUser;
                        }
                        //Sending e-mail
                        String fullPath = getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH);
                        if (userInSession.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
                            EmailSender.sendRegistrationEmailToEngineer(registeredUser, password, fullPath);
                        } else {
                            EmailSender.sendRegistrationEmailToCustomer(registeredUser, password, fullPath);
                        }
                        //Checking for user selecting attributes to create a new connection
                        Service service = (Service) request.getSession()
                                .getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
                        ServiceLocation servLoc = (ServiceLocation) request.getSession()
                                .getAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
                        request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
                        if (userInSession.getUserRole().equals(UserRole.CUSTOMER.toString()) && service != null
                                && servLoc != null) {
                            //Redirect to servlet of creating new connection
                            ServletContext context = getServletContext();
                            RequestDispatcher rd = context.getRequestDispatcher(CauliflowerInfo.PROCEED_CONTROLLER_LINK);
                            rd.forward(request, response);
                        } else {
                            //Redirect to dashboard of user, who has registered new user
                            if (userInSession.getUserRole().equals(UserRole.CUSTOMER.toString()))
                                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                            if (userInSession.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
                                request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,
                                        CauliflowerInfo.OK_REGISTER_EMPLOYEE_MESSAGE);
                                response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                            }
                        }
                    } else {
                        //Insertion attribute of uniqueness phone number error into session
                        // and redirect to registration page
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE,
                                CauliflowerInfo.PHONE_UNIQ_ERROR_MESSAGE);
                        response.sendRedirect(CauliflowerInfo.AUTH_LINK);
                    }
                }else{
                    //Insertion attribute of uniqueness e-mail error into session and redirect to registration page
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.EMAIL_UNIQ_ERROR_MESSAGE);
                    response.sendRedirect(CauliflowerInfo.AUTH_LINK);
                }
            } catch (SQLException e) {
                e.printStackTrace();
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