package com.naukma.cauliflower.controllers;

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
    /*
     ACK.5
     ACK.6
     ACK.7
     ACK.8
     ACK.9 ~~

     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //List<ProviderLocation> providerLocations = DAO.INSTANCE.getProviderLocations();
        //request.setAttribute("providerlocations", providerLocations);
        //request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
