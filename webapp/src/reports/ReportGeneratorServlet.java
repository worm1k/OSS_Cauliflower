package reports;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;

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
        Writer out = response.getWriter();
        //ResultSet rs;
      //  XLSReportGenerator reportGenerator = new XLSReportGenerator("aaa",rs);
       // reportGenerator.createXlsFile("aaa");
        out.write("HelloWorld");


    }
}
