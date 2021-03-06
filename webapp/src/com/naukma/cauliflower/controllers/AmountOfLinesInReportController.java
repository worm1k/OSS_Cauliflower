package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Eugene on 17.12.2014.
 */
@WebServlet(name = "AmountOfLinesInReportController")
public class AmountOfLinesInReportController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String methodName = (String) request.getParameter("reportMethod");
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        java.sql.Date sqlStartDate = null;
        java.sql.Date sqlEndDate = null;
        if (startDate != null && endDate != null)
            try{
        //parce date from jquery to sql
                java.util.Date date = formatter.parse(startDate);
                sqlStartDate = new java.sql.Date(date.getTime());

                java.util.Date date2 = formatter.parse(endDate);
                sqlEndDate = new java.sql.Date(date2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        int result = 0;
        try {
            if (methodName.equals("Devices")) {
                result = DAO.INSTANCE.getDevicesReportLinesAmount();
            } else if (methodName.equals("Circuits")) {
                result = DAO.INSTANCE.getCircuitsReportLinesAmount();
            } else if (methodName.equals("Cables")) {
                result = DAO.INSTANCE.getCablesReportLinesAmount();
            } else if (methodName.equals("Ports")) {
                result = DAO.INSTANCE.getPortsReportLinesAmount();
            } else if (methodName.equals("Profitable")) {
                result = DAO.INSTANCE.getMostProfitableRouterReportLinesAmount();
            } else if (methodName.equals("utilizationAndCapacity")){
                result = DAO.INSTANCE.getUsedRoutersAndCapacityOfPortsLinesAmount();
            }else if (methodName.equals("Profitability")) {
                result = DAO.INSTANCE.getProfitabilityByMonthLinesAmount();
            }else if (methodName.equals("New") && startDate != null && endDate != null) {
                result = DAO.INSTANCE.getOrdersPerPeriodLinesAmount(Scenario.NEW, sqlStartDate, sqlEndDate);
            }else if (methodName.equals("Disconnect") && startDate != null && endDate != null) {
                result = DAO.INSTANCE.getOrdersPerPeriodLinesAmount(Scenario.DISCONNECT, sqlStartDate, sqlEndDate);
            }else if (methodName.equals("Tree")){
                result = DAO.INSTANCE.getCIALinesAmount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        mapper.writeValue(out, result);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(CauliflowerInfo.HOME_LINK);
    }
}
