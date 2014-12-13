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
                int serviceOrderId = Integer.parseInt(serviceOrderIdParam);
                Task task = DAO.INSTANCE.getTaskById(taskId);


                if (task.getTaskStatus().equals(TaskStatus.PROCESSING.toString()) &&
                    user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor(UserRole.INSTALLATION_ENG)) {

                    if (task.getTaskName().equals(TaskName.CREATE_NEW_ROUTER)) {
                        DAO.INSTANCE.createRouter();
                    } else if (task.getTaskName().equals(TaskName.CREATE_CIRCUIT)) {
                        if (DAO.INSTANCE.freePortExists()) {
                            DAO.INSTANCE.createPortAndCableAndAssignToServiceInstance(serviceOrderId);
                            DAO.INSTANCE.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE,TaskStatus.FREE);
                        } else {
                            if (DAO.INSTANCE.countNotCompletedTasksByTaskName(TaskName.CREATE_NEW_ROUTER) == 0) {
                                DAO.INSTANCE.createNewTask(serviceOrderId, UserRole.INSTALLATION_ENG, TaskName.CREATE_NEW_ROUTER, TaskStatus.FREE);
                            }
                            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.NO_PORTS_ERROR_MESSAGE);
                        }
                    } else if (task.getTaskName().equals(TaskName.BREAK_CIRCUIT)) {
                        DAO.INSTANCE.removeCableFromServiceInstanceAndFreePort(serviceOrderId);
                        DAO.INSTANCE.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.DISCONNECT_INSTANCE,TaskStatus.FREE);
                    }

                    DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
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
