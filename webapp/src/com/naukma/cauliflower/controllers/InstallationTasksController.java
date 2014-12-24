package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.mail.EmailSender;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This servlet controls tasks completion by installation engineer. There are three types of tasks for him:
 * create new router(simply creates a new router), create circuit (if free port exists, then
 * free port is taken and marked as used, new cable created and associated with this port, and connected to service instance,
 * also creates a new task for provisioning engineer to activate the service instance. If free port doesn't exist
 * then system checks if task to create new router already exists in system, if it doesn't then one
 * more task for installation engineer to create a new router is created. Whether this task exists or not,
 * in both scenarios servlet sets session error attribute so that jsp could notify user that he should
 * complete a task for new router) and break circuit(cable is removed, port marked as free and new task
 * for provisioning engineer to disconnect instance is created). Finally servlet redirects to installation
 * dashboard page. Every time task is created, e-mail notifications are send to specified user group(provisioning or
 * installation engineer group). For all scenarios(besides when free port doesn't exist) current task will be marked as
 * completed.
 */
@WebServlet(name = "InstallationTasksController")
public class InstallationTasksController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(InstallationTasksController.class);
    DAO dao = DAO.INSTANCE;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get all required attributes and parameters
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String taskIdParam = request.getParameter(CauliflowerInfo.TASK_ID_PARAM);
        String serviceOrderIdParam = request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID);
        //make sure that user is not null
        if (user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
            return;
        }
        if (taskIdParam == null || serviceOrderIdParam == null) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            return;
        }

        try {
            int taskId = Integer.parseInt(taskIdParam);
            int serviceOrderId = Integer.parseInt(serviceOrderIdParam);
            Task task = dao.getTaskById(taskId);

            //this servlet should work only if task is processing and userRole is installation engineer
            if (!task.getTaskStatus().equals(TaskStatus.PROCESSING.toString()) ||
                user.getUserRoleId() != dao.getUserRoleIdFor(UserRole.INSTALLATION_ENG)) {
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
                return;
            }

                if (task.getTaskName().equals(TaskName.CREATE_NEW_ROUTER)) {
                    //create new router and make task completed
                    dao.createRouter();
                    dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                } else if (task.getTaskName().equals(TaskName.CREATE_CIRCUIT)) {
                    //check whether free ports exist
                    if (dao.freePortExists()) {
                        dao.createPortAndCableAndAssignToServiceInstance(serviceOrderId);
                        dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                        dao.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE, TaskStatus.FREE);
                        sendEmailNotificationForUserGroup(UserRole.PROVISIONING_ENG, TaskName.CONNECT_INSTANCE);
                    } else {
                        //if no free ports exist check whether not completed task to create new router already exists
                        if (dao.countNotCompletedTasksByTaskName(TaskName.CREATE_NEW_ROUTER) == 0) {
                            dao.createNewTask(serviceOrderId, UserRole.INSTALLATION_ENG, TaskName.CREATE_NEW_ROUTER, TaskStatus.FREE);
                            sendEmailNotificationForUserGroup(UserRole.INSTALLATION_ENG, TaskName.CREATE_NEW_ROUTER);
                        }
                        //set error attribute so that jsp could notify user that he should complete task to create a new router
                        request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.NO_PORTS_ERROR_MESSAGE);
                    }
                } else if (task.getTaskName().equals(TaskName.BREAK_CIRCUIT)) {
                    dao.removeCableFromServiceInstanceAndFreePort(serviceOrderId);
                    dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                    dao.createNewTask(serviceOrderId, UserRole.PROVISIONING_ENG, TaskName.DISCONNECT_INSTANCE,TaskStatus.FREE);
                    sendEmailNotificationForUserGroup(UserRole.PROVISIONING_ENG, TaskName.DISCONNECT_INSTANCE);
                }

                response.sendRedirect(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK);
                return;

        } catch(SQLException e) {
            logger.error(e);
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
        }
    }

    /**
     * Send email to selected user group to notify that new task was created.
     * @param userRole user group
     * @param taskName task that was created
     * @throws SQLException
     */
    private void sendEmailNotificationForUserGroup(UserRole userRole, TaskName taskName) throws SQLException{
        List<User> usersByUserRole = dao.getUsersByUserRole(userRole);
        EmailSender.sendEmailToGroup(usersByUserRole,
                taskName.toString(),
                getServletContext().getRealPath(CauliflowerInfo.EMAIL_TEMPLATE_PATH));

    }
}
