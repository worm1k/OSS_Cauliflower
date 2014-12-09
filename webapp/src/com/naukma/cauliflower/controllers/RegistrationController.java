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
 */
@WebServlet(name = "RegistrationController")
public class RegistrationController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginController.class);
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pathFrom  = request.getHeader("Referer");
        String userRole;
        userRole = request.getParameter("userRole");
        if (userRole.length() <= 0) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.USERROLE_ERROR_MESSAGE);
            response.sendRedirect(pathFrom);
        }
        User userInSession = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if((userInSession==null && userRole.equals(UserRole.CUSTOMER.toString())) ||
                (userInSession!=null && userInSession.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                        && !userRole.equals(UserRole.CUSTOMER.toString()))) {
            String email;
            String password;
            String firstName;
            String lastName;
            String phone;
            int userRoleId=0;
            try {
                if(userRole.equals(UserRole.CUSTOMER.toString())) userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.CUSTOMER);
                if(userRole.equals(UserRole.PROVISIONING_ENG.toString())) userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.PROVISIONING_ENG);
                if(userRole.equals(UserRole.CUST_SUP_ENG.toString())) userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.CUST_SUP_ENG);
                if(userRole.equals(UserRole.INSTALLATION_ENG.toString())) userRoleId = DAO.INSTANCE.getUserRoleIdFor(UserRole.INSTALLATION_ENG);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (userRoleId <= 0) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.USERROLEID_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }

            email = request.getParameter("email").toLowerCase();
            if (email == null || email.length() < 6) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.EMAIL_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
            password = (String) request.getParameter("password");
            if (password == null || password.length() < 6) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PASSWORD_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
            firstName = (String) request.getParameter("name");
            if (firstName == null || firstName.length() < 2) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.FIRSTNAME_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
            lastName = (String) request.getParameter("surname");
            if (lastName == null || lastName.length() < 2) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.LASTNAME_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
            phone = (String) request.getParameter("phone");
            if (phone == null || phone.length() < 10) {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PHONE_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }

            if (DAO.INSTANCE.checkForEmailUniq(email)) {
                if (DAO.INSTANCE.checkForPhoneUniq(phone)) {
                    User createdUser = new User(0,userRoleId, userRole, email, firstName, lastName, phone,false);
                    {//help
                        System.out.println(createdUser);
                    }
                    //hashing password
                    String hashedPassword= Cryptographer.hmacSha1(password);
                    logger.info(" reg controller :: hashed password form"+password+" is "+hashedPassword);
                    //
                    int createdUserId = DAO.INSTANCE.createUser(createdUser, hashedPassword);
                    if (createdUserId > 0) {
                        createdUser = new User(createdUserId, userRoleId, userRole, email, firstName, lastName, phone,false);
                        if(userInSession==null){
                            request.getSession().setAttribute(CauliflowerInfo.USER_ATTRIBUTE, createdUser);
                            userInSession=createdUser;
                        }
                        String fullPath = getServletContext().getRealPath("/WEB-INF/mail/");
                        EmailSender.sendEmail(createdUser, EmailSender.SUBJECT_REGISTRATION, password, EmailSender.getTemplate("/regTemplate.ftl", fullPath));
                        Service service = (Service) request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
                        ServiceLocation servLoc = (ServiceLocation) request.getSession().getAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
                        request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
                        if (userInSession.getUserRole().equals(UserRole.CUSTOMER.toString()) && service != null && servLoc != null) {
                            ServletContext context = getServletContext();
                            RequestDispatcher rd = context.getRequestDispatcher("/proceed");
                            rd.forward(request, response);
                        } else {
                            if (userInSession.getUserRole().equals(UserRole.CUSTOMER.toString())) response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
                            if (userInSession.getUserRole().equals(UserRole.ADMINISTRATOR.toString())) {
                                request.getSession().setAttribute(CauliflowerInfo.OK_ATTRIBUTE,CauliflowerInfo.OK_REGISTER_EMPLOYEE_MESSAGE);
                                response.sendRedirect(CauliflowerInfo.ADMIN_DASHBOARD_LINK);
                            }
                        }
                    } else {
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
                        response.sendRedirect(pathFrom);
                    }
                } else {
                    request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PHONE_UNIQ_ERROR_MESSAGE);
                    response.sendRedirect(pathFrom);
                }
            } else {
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.EMAIL_UNIQ_ERROR_MESSAGE);
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