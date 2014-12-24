package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Артем on 05.12.2014.
 *
 * This servlet controls logout of users.
 */
@WebServlet(name = "LogoutController")
public class LogoutController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Invalidation of session and redirect to home page.
        request.getSession().invalidate();
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}
