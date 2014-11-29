package com.naukma.cauliflower.reports;

import com.naukma.cauliflower.dao.DAO;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;

/**
 * Created by Max on 26.11.2014.
 */
@WebServlet(name = "ReportGeneratorServlet")
public class ReportGeneratorServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=report.xls");

        XLSReportGenerator reportGenerator = null;
        try {
            reportGenerator = new XLSReportGenerator("aaa", DAO.INSTANCE.reportTester());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ServletOutputStream outputStream = response.getOutputStream();
        reportGenerator.createXlsFile().write(outputStream);
        outputStream.flush();
        outputStream.close();



    }
}
