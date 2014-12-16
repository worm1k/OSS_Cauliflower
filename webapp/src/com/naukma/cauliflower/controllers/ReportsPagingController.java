package com.naukma.cauliflower.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Eugene on 15.12.2014.
 */
@WebServlet(name = "ReportsPagingController")
public class ReportsPagingController  extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        int page = Integer.valueOf((String) request.getParameter("page"));
        String methodName = (String) request.getParameter("reportMethod");
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");
        boolean hasRights = false;
        User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String pathFrom  = request.getHeader("Referer");

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


        List<Object> list = null;
        try {
            if (methodName.equals("Devices")) {
                //resultSet = DAO.INSTANCE.getDevicesForReport();
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());
                if(hasRights)
                    list = DAO.INSTANCE.getDevicesForReport(page);
            } else if (methodName.equals("Circuits")) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getCircuitsForReport(page);
            } else if (methodName.equals("Cables")) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getCablesForReport(page);
            } else if (methodName.equals("Ports")) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getPortsForReport(page);
            } else if (methodName.equals("Profitable")) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                //resultSet = DAO.INSTANCE.getMostProfitableRouterForReport();
                if(hasRights)
                    list = DAO.INSTANCE.getMostProfitableRouterForReport(page);
            } else if (methodName.equals("utilizationAndCapacity")){
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getUsedRoutersAndCapacityOfPorts(page);
            }else if (methodName.equals("Profitability")) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getProfitabilityByMonth(page);
            }else if (methodName.equals("New") && startDate != null && endDate != null) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getOrdersPerPeriod(Scenario.NEW, sqlStartDate, sqlEndDate, page);
                //resultSet = DAO.INSTANCE.getNewOrdersPerPeriod(sqlStartDate, sqlEndDate);
            }else if (methodName.equals("Disconnect") && startDate != null && endDate != null) {
                hasRights =
                        user.getUserRole().equals(UserRole.ADMINISTRATOR.toString());
                //resultSet =
                if(hasRights)
                    list = DAO.INSTANCE.getOrdersPerPeriod(Scenario.DISCONNECT, sqlStartDate, sqlEndDate, page);
            }
            //resultSet = DAO.INSTANCE.DisconnectOrdersPerPeriod(sqlStartDate, sqlEndDate);
            /*if (resultSet == null)
                resultSet = DAO.INSTANCE.reportTester();*/

            //reportGenerator = new XLSReportGenerator("aaa", resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(hasRights) {
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
//        //return data as json object
//        //Convert Java object to JSON format
            mapper.writeValue(out, list);
//        Service service = DAO.INSTANCE.getServiceById(1);
//        mapper.writeValue(out, service);
        }else{
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
            //response.getWriter().println("Sorry, you have no rights for downloading this report");
            response.sendRedirect(pathFrom);
        }

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}