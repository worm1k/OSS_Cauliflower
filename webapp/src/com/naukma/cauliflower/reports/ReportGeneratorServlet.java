package com.naukma.cauliflower.reports;

import com.naukma.cauliflower.dao.DAO;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
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
        String methodName = (String) request.getAttribute("reportMethod");

        Method method = null;
        ResultSet resultSet = null;
        if(methodName!=null && methodName!= "")
            try {
                method = DAO.INSTANCE.getClass().getMethod(methodName);
                resultSet = (ResultSet) method.invoke(DAO.INSTANCE);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        XLSReportGenerator reportGenerator = null;
        try {

            if(resultSet == null ) resultSet = DAO.INSTANCE.reportTester();
            reportGenerator = new XLSReportGenerator("aaa", resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
         ServletOutputStream outputStream = response.getOutputStream();
        reportGenerator.createXlsFile().write(outputStream);
        outputStream.flush();
        outputStream.close();



    }
}
