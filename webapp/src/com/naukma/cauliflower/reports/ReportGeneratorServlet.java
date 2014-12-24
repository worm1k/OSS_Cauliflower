package com.naukma.cauliflower.reports;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

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
        boolean hasRights = false;
        User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String pathFrom  = request.getHeader("Referer");

        if(user == null)
            response.sendRedirect(CauliflowerInfo.HOME_LINK);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        java.sql.Date sqlStartDate = null;
        java.sql.Date sqlEndDate = null;
        if (startDate != null && endDate != null)
            try {
                //parce date from jquery to sql
                java.util.Date date = formatter.parse(startDate);
                sqlStartDate = new java.sql.Date(date.getTime());

                java.util.Date date2 = formatter.parse(endDate);
                sqlEndDate = new java.sql.Date(date2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ReportGenerator reportGenerator = null;
            try {
                if (methodName.equals("Devices")) {
                     hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());

                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getDevicesForReport(EXT);
                } else if (methodName.equals("Circuits")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());


                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getCircuitsForReport(EXT);
                } else if (methodName.equals("Cables")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getCablesForReport(EXT);
                } else if (methodName.equals("Ports")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getPortsForReport(EXT);
                } else if (methodName.equals("Profitable")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());

                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getMostProfitableRouterForReport(EXT);
                } else if (methodName.equals("utilizationAndCapacity")){
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getUsedRoutersAndCapacityOfPorts(EXT);
                }else if (methodName.equals("Profitability")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString())
                                    || user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getProfitabilityByMonth(EXT);
                }else if (methodName.equals("New") && startDate != null && endDate != null) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getOrdersPerPeriod(Scenario.NEW, sqlStartDate, sqlEndDate, EXT);
                }else if (methodName.equals("Disconnect") && startDate != null && endDate != null) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getOrdersPerPeriod(Scenario.DISCONNECT, sqlStartDate, sqlEndDate, EXT);
                }else if (methodName.equals("Tree")) {
                    hasRights =
                            user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()) ||
                                    user.getUserRole().equals(UserRole.CUST_SUP_ENG.toString()) ||
                                            user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString());
                    if(hasRights)
                        reportGenerator = DAO.INSTANCE.getCIAReport(EXT);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(hasRights) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + methodName + "Report." + EXT);
                ServletOutputStream outputStream = response.getOutputStream();
                reportGenerator.writeInStream(outputStream);
                outputStream.flush();
                outputStream.close();
            }else{
                request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.PERMISSION_ERROR_MESSAGE);
                response.sendRedirect(pathFrom);
            }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
