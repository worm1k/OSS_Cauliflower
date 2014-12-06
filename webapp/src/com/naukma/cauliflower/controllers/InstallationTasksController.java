package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "InstallationTasksController")
public class InstallationTasksController extends HttpServlet {

    /*
    RI.9
    SOW.6 ( RI.1 ) + CREATE NEXT TASK
    SOW.5
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if (user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }

        Task task = (Task) request.getAttribute(CauliflowerInfo.TASK_PARAM);
        int taskId = task.getTaskId();
        int serviceOrderId = task.getServiceOrderId();

        try {
            //RI.9
            //The system should allow creating Devices, Ports and Cables only by Installation Engineer
            if (DAO.INSTANCE.getTaskStatus(taskId) == TaskStatus.PROCESSING &&
                //user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor_InstallationEngineer()) {
                    user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor(UserRoles.INSTALLATION_ENG)) {

                Scenario scenario = DAO.INSTANCE.getOrderScenario(serviceOrderId);
                if (scenario == Scenario.NEW) {

                    if (!DAO.INSTANCE.freePortExists())
                        DAO.INSTANCE.createRouter();
                    DAO.INSTANCE.createPortAndCableAndAssignToServiceInstance(serviceOrderId);
                } else if (scenario == Scenario.DISCONNECT) {
                    DAO.INSTANCE.removeCableFromServiceInstanceAndFreePort(serviceOrderId);
                }
                DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                DAO.INSTANCE.createNewTask(serviceOrderId, UserRoles.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);

            } else
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
