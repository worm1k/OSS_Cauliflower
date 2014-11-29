package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "LocationController")
public class LocationController {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String locationAddress = (String) request.getAttribute("address");
        Integer locationLongitude = (Integer) request.getAttribute("longitude");
        Integer locationLatitude = (Integer) request.getAttribute("latitude");
        ServiceLocation serviceLocation = (ServiceLocation) request.getSession().getAttribute("serviceLocation");
        if (serviceLocation != null) {
            serviceLocation.setLocationAddress(locationAddress);
            serviceLocation.setLocationLongitude(locationLongitude);
            serviceLocation.setLocationLatitude(locationLatitude);
        } else {
            serviceLocation = new ServiceLocation(-1, locationAddress, locationLongitude, locationLatitude);
        }
        request.getSession().setAttribute("serviceLocation", serviceLocation);

        //request.setAttribute("tasks", tasks);
        //request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
