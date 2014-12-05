package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
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


@WebServlet(name = "GetServicesController")
public class GetServicesController extends HttpServlet {

    private static final  String GO_TO_AUTHENTICATION="auth.jsp";
    private static final String PROCEED_TO_ORDER_CONTROLLER="/proceed";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Service> services = null;
        try {
            services = DAO.INSTANCE.getServices();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
//
//        //return data as json object
//        //Convert Java object to JSON format
        mapper.writeValue(out, services);

//        Service service = DAO.INSTANCE.getServiceById(1);
//        mapper.writeValue(out, service);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serviceLocationAddress = req.getParameter("serviceLocationAddress");
        String serviceLocationLongtitude = req.getParameter("serviceLocationLongtitude");
        String serviceLocationLatitude = req.getParameter("serviceLocationLatitude");

        String serviceId = req.getParameter("serviceId");

        Service service = null;
        ServiceLocation serviceLocation = null;

        try{
            serviceLocation = new ServiceLocation(
                    0,
                    serviceLocationAddress,
                    Double.parseDouble(serviceLocationLongtitude),
                    Double.parseDouble(serviceLocationLatitude)
            );
            service = DAO.INSTANCE.getServiceById(Integer.parseInt(serviceId));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        HttpSession session = req.getSession();
        session.setAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE,service);
        session.setAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE,serviceLocation);

        final User user  = (User) session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if(user == null){
            /*RequestDispatcher requestDispatcher = req.getRequestDispatcher(GO_TO_AUTHENTICATION);
            requestDispatcher.forward(req,resp);*/
            resp.sendRedirect("auth.jsp");
        }
        else
        {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher(PROCEED_TO_ORDER_CONTROLLER);
            requestDispatcher.forward(req,resp);
        }

    }
}
