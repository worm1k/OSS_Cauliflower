package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.Service;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "GetServicesController")
public class GetServicesController extends HttpServlet {
     /*
     ACK.5
     ACK.6
     ACK.7
     ACK.8
     ACK.9 ~~
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Service> services = DAO.INSTANCE.getServices();
        request.setAttribute("services", services);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        ToDO: Only for test purposes (Vladmyr)
        ObjectMapper mapper = new ObjectMapper();

        Service service = new Service(1, "locationAddress", 1, 1, "serviceTypeName", "serviceSpeed", 1, 1, 0);

        List<Service> lstService = new ArrayList<Service>();
        lstService.add(service);
        lstService.add(service);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        //return data as json object
        mapper.writeValue(out, lstService);
    }
}
