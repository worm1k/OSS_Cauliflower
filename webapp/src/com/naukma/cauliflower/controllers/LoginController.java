package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import org.apache.log4j.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String res = "";
        Service service = (Service)request.getSession().getAttribute("service");
        ServiceLocation servLoc = (ServiceLocation)request.getSession().getAttribute("serviceLocation");

        User user = null;
        user = DAO.INSTANCE.getUserByLoginAndPassword(username, password);
        if(user == null) {
            request.getSession().setAttribute("error","Incorrect login or password!");
            response.sendRedirect(pathFrom);
        }else{
            request.getSession().setAttribute("user", user);
            logger.info(" LOGGER ::   LoginController  : user is" + user.getFirstName());
            if(service!=null && servLoc!=null){
                ServletContext context = getServletContext();
                RequestDispatcher rd = context.getRequestDispatcher("/proceed");
                rd.forward(request, response);
            }else{
                response.sendRedirect("dashboard.jsp");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
