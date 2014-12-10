package com.naukma.cauliflower.reports;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.Scenario;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Max on 26.11.2014.
 */
@WebServlet(name = "ReportGeneratorServlet")
public class ReportGeneratorServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String methodName = (String) request.getParameter("reportMethod");
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");
        final String EXT = request.getParameter("extension");

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

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename="+methodName+"Report."+EXT);
            ReportGenerator reportGenerator = null;
            try {
                if (methodName.equals("Devises"))
                    //resultSet = DAO.INSTANCE.getDevicesForReport();
                    reportGenerator = DAO.INSTANCE.getDevicesForReport(EXT);
                else if (methodName.equals("Circuits"))
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getCircuitsForReport(EXT);
                else if (methodName.equals("Cables"))
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getCablesForReport(EXT);
                else if (methodName.equals("Ports"))
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getPortsForReport(EXT);
                else if (methodName.equals("Profitable"))
                    //resultSet = DAO.INSTANCE.getMostProfitableRouterForReport();
                    reportGenerator = DAO.INSTANCE.getMostProfitableRouterForReport(EXT);
                else if (methodName.equals("utilizationAndCapacity"))
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getUsedRoutersAndCapacityOfPorts(EXT);
                else if (methodName.equals("Profitability"))
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getProfitabilityByMonth(EXT);
                else if (methodName.equals("New") && startDate != null && endDate != null)
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getOrdersPerPeriod(Scenario.NEW, sqlStartDate, sqlEndDate, EXT);
                    //resultSet = DAO.INSTANCE.getNewOrdersPerPeriod(sqlStartDate, sqlEndDate);
                else if (methodName.equals("Disconnect") && startDate != null && endDate != null)
                    //resultSet =
                    reportGenerator = DAO.INSTANCE.getOrdersPerPeriod(Scenario.DISCONNECT, sqlStartDate, sqlEndDate, EXT);
                //resultSet = DAO.INSTANCE.DisconnectOrdersPerPeriod(sqlStartDate, sqlEndDate);
            /*if (resultSet == null)
                resultSet = DAO.INSTANCE.reportTester();*/

                //reportGenerator = new XLSReportGenerator("aaa", resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ServletOutputStream outputStream = response.getOutputStream();
            reportGenerator.writeInStream(outputStream);
            outputStream.flush();
            outputStream.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
