package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Eugene on 17.12.2014.
 */
@WebServlet(name = "AmountOfLinesInReportComtroller")
public class AmountOfLinesInReportComtroller   extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String methodName = (String) request.getParameter("reportMethod");
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date sqlStartDate = null;
        java.sql.Date sqlEndDate = null;
        if (startDate != null && endDate != null)
            try {

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
                //resultSet = DAO.INSTANCE.getNewOrdersPerPeriod(sqlStartDate, sqlEndDate);
            }else if (methodName.equals("Disconnect") && startDate != null && endDate != null) {
                result = DAO.INSTANCE.getOrdersPerPeriodLinesAmount(Scenario.DISCONNECT, sqlStartDate, sqlEndDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.getWriter().println(result);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final User user = (User) req.getSession().getAttribute(CauliflowerInfo.USER_ID_ATTRIBUTE);

        if(user == null){
            resp.sendRedirect(CauliflowerInfo.HOME_LINK);
        }else if(user.getUserRole().equals(UserRole.CUSTOMER.toString())){
            resp.sendRedirect(CauliflowerInfo.HOME_LINK);
        }else{
            try{
                req.setAttribute(CauliflowerInfo.REPORT_METHOD_ATTRIBUTE, req.getParameter(CauliflowerInfo.REPORT_METHOD_ATTRIBUTE));
                resp.sendRedirect(CauliflowerInfo.REPORT_VIEW_LINK);
            }catch (Exception e){
                resp.sendRedirect(CauliflowerInfo.HOME_LINK);
            }
        }
    }
}
