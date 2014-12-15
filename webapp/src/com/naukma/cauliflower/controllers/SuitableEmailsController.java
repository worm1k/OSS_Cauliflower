package com.naukma.cauliflower.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ihor
 */
@WebServlet(name = "SuitableEmailsController", urlPatterns = "/autocomplete")
public class SuitableEmailsController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SuitableEmailsController.class);
    private static final int MAX_EMAILS_COUNT = 10;
    private static final int START = 0;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailLike = request.getParameter("query");

        logger.info("EmailsServlet :: emailLike = " + emailLike);
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("%");
            queryString.append(emailLike);
            queryString.append("%");
            List<String> emails = DAO.INSTANCE.getEmailsLike(queryString.toString());
            logger.info("EmailsServlet :: emailLike sizeeee = " + emails.size());
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            if (emails.size() > MAX_EMAILS_COUNT) {
                emails = emails.subList(START, MAX_EMAILS_COUNT);
            }
            PrintWriter out = response.getWriter();
//        //Convert Java object to JSON format
            mapper.writeValue(out, emails);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
