package com.naukma.cauliflower.reports;

import com.naukma.cauliflower.dao.DAO;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Max on 26.11.2014.
 */
@WebServlet(name = "ReportGeneratorServlet")
public class ReportGeneratorServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=report.xls");
        String methodName = (String) request.getParameter("reportMethod");
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");

        System.out.println(startDate+" "+endDate);

        ResultSet resultSet = null;

        XLSReportGenerator reportGenerator = null;
        try {
        if(methodName.equals("Devises"))
            resultSet = DAO.INSTANCE.getDevicesForReport();
        else if(methodName.equals("Circuits"))
            resultSet = DAO.INSTANCE.getCircuitsForReport();
        else if(methodName.equals("Cables"))
            resultSet = DAO.INSTANCE.getCablesForReport();
        else if(methodName.equals("Ports"))
            resultSet = DAO.INSTANCE.getPortsForReport();
        else if(methodName.equals("Profitable"))
            resultSet = DAO.INSTANCE.getMostProfitableRouterForReport();
        else if(methodName.equals("utilizationAndCapacity"))
            resultSet = DAO.INSTANCE.getUsedRoutersAndCapacityOfPorts();
        else if(methodName.equals("Profitability"))
            resultSet = DAO.INSTANCE.getProfitabilityByMonth();
        if(resultSet == null)
            resultSet = DAO.INSTANCE.reportTester();

            reportGenerator = new XLSReportGenerator("aaa", resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ServletOutputStream outputStream = response.getOutputStream();
        reportGenerator.createXlsFile().write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    }
}
