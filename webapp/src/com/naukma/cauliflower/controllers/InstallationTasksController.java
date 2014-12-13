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
    private static final int PORTS_PER_ROUTER = 60;
    private static final int PORTS_FREED_PER_DISCONNECT = 1;

    /*
    RI.9
    SOW.6 ( RI.1 ) + CREATE NEXT TASK
    SOW.5
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String taskIdParam = request.getParameter(CauliflowerInfo.TASK_ID_PARAM);
        String serviceOrderIdParam = request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID);
        if (user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        } else if (taskIdParam == null || serviceOrderIdParam == null) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        } else {
            try {
                int taskId = Integer.parseInt(taskIdParam);
                Task task = DAO.INSTANCE.getTaskById(taskId);
                int serviceOrderId = Integer.parseInt(serviceOrderIdParam);
                //RI.9
                //The system should allow creating Devices, Ports and Cables only by Installation Engineer
                if (task.getTaskStatus().equals(TaskStatus.PROCESSING.toString()) &&
                        //user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor_InstallationEngineer()) {
                        user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor(UserRole.INSTALLATION_ENG)) {
                    if (task.getTaskName() == TaskName.CREATE_NEW_ROUTER) {
                        DAO.INSTANCE.createRouter();
                        DAO.INSTANCE.activateWaitingTasks(PORTS_PER_ROUTER);
                    } else if (task.getTaskName() == TaskName.CONNECT_NEW_PERSON) {
                        DAO.INSTANCE.createPortAndCableAndAssignToServiceInstance(serviceOrderId);
                    } else if (task.getTaskName() == TaskName.BREAK_CIRCUIT) {
                        DAO.INSTANCE.removeCableFromServiceInstanceAndFreePort(serviceOrderId);
                        DAO.INSTANCE.activateWaitingTasks(PORTS_FREED_PER_DISCONNECT);
                    }
                    DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                    DAO.INSTANCE.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE,TaskStatus.FREE);
                    response.sendRedirect(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK);

                } else
                    response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
