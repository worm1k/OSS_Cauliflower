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
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "InstallationTasksController")
public class InstallationTasksController extends HttpServlet {
    DAO dao = DAO.INSTANCE;

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
                Task task = dao.getTaskById(taskId);
                if (task.getTaskStatus().equals(TaskStatus.PROCESSING.toString()) &&
                    user.getUserRoleId() == dao.getUserRoleIdFor(UserRole.INSTALLATION_ENG)) {

                    if (task.getTaskName().equals(TaskName.CREATE_NEW_ROUTER)) {
                        dao.createRouter();
                        dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                    } else if (task.getTaskName().equals(TaskName.CREATE_CIRCUIT)) {
                        if (dao.freePortExists()) {
                            dao.createPortAndCableAndAssignToServiceInstance(serviceOrderId);
                            dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                            dao.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE, TaskStatus.FREE);
                        } else {
                            if (dao.countNotCompletedTasksByTaskName(TaskName.CREATE_NEW_ROUTER) == 0) {
                                dao.createNewTask(serviceOrderId, UserRole.INSTALLATION_ENG, TaskName.CREATE_NEW_ROUTER, TaskStatus.FREE);
                            }
                            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.NO_PORTS_ERROR_MESSAGE);
                        }
                    } else if (task.getTaskName().equals(TaskName.BREAK_CIRCUIT)) {
                        dao.removeCableFromServiceInstanceAndFreePort(serviceOrderId);
                        dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                        dao.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.DISCONNECT_INSTANCE,TaskStatus.FREE);
                    }

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
