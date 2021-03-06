package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**

 */

@WebServlet(name = "NewOrderController")
public class NewOrderController extends HttpServlet {

    private static final String PROCEED_TO_ORDER_CONTROLLER="/proceed";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathFrom  = req.getHeader("Referer");
        HttpSession session = req.getSession();
        final User user  = (User) session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(user==null || user.getUserRole().equals(UserRole.CUSTOMER.toString())) {
            String serviceLocationAddress = req.getParameter("serviceLocationAddress");
            String serviceLocationLongtitude = req.getParameter("serviceLocationLongtitude");
            String serviceLocationLatitude = req.getParameter("serviceLocationLatitude");

            String serviceId = req.getParameter("serviceId");

            Service service = null;
            ServiceLocation serviceLocation = null;

            try {
                serviceLocation = new ServiceLocation(
                        0,
                        serviceLocationAddress,
                        Double.parseDouble(serviceLocationLongtitude),
                        Double.parseDouble(serviceLocationLatitude)
                );
                service = DAO.INSTANCE.getServiceById(Integer.parseInt(serviceId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            session.setAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE, service);
            session.setAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE, serviceLocation);

            if (user == null) {

                resp.sendRedirect(CauliflowerInfo.AUTH_LINK);

            } else {
                RequestDispatcher requestDispatcher = req.getRequestDispatcher(PROCEED_TO_ORDER_CONTROLLER);
                requestDispatcher.forward(req, resp);
            }
        }else{
            req.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            resp.sendRedirect(pathFrom);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}
