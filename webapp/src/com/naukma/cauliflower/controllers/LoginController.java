package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String res = "";
        Service service = (Service)request.getSession().getAttribute(CauliflowerInfo.serviceAttribute);
        ServiceLocation servLoc = (ServiceLocation)request.getSession().getAttribute(CauliflowerInfo.serviceLocationAttribute);

        User user = null;
        try {
            user = DAO.INSTANCE.getUserByLoginAndPassword(username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(user == null) {
            request.getSession().setAttribute(CauliflowerInfo.errorAttribute,"Incorrect login or password!");
            response.sendRedirect(pathFrom);
        }else{
            request.getSession().setAttribute(CauliflowerInfo.userAttribute, user);
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
        HttpSession session = request.getSession();
        final User user = (User)session.getAttribute(CauliflowerInfo.userAttribute);
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter out = response.getWriter();

        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(out, user);
    }
}
