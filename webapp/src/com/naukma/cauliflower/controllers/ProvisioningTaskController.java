package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This servlet controls tasks completion by installation engineer. First it checks whether session user attribute
 * and request task id parameter exist. Then servlet checks if task received is with status processing and that user
 * role is provisioning engineer. There are three tasks that may be completed by provisioning engineer. They are: connect
 * instance(activates service instance), modify service(changes service for service instance), disconnect instance
 * (disconnects service instance). After one of them is processed, both task and order associated with it are marked
 * as completed and service instance is marked as not blocked. Finally servlet redirects to provisioning engineer
 * dashboard.
 */
@WebServlet(name = "ProvisioningTaskController")
public class ProvisioningTaskController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ProvisioningTaskController.class);
    DAO dao = DAO.INSTANCE;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get all required attributes and parameters
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String taskIdParam = request.getParameter(CauliflowerInfo.TASK_ID_PARAM);

        //make sure that user is not null
        if (user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
            return;
        }

        //make sure that task id is not null, otherwise set error attribute and redirect
        if (taskIdParam == null) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            return;
        }

        try {
            Integer taskId = Integer.parseInt(taskIdParam);
            Task task = dao.getTaskById(taskId);

            //this servlet should work only if task is processing and userRole is provisioning engineer
            if (!task.getTaskStatus().equals(TaskStatus.PROCESSING.toString())  ||
                user.getUserRoleId() != dao.getUserRoleIdFor(UserRole.PROVISIONING_ENG)) {
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
                return;
            }

            ServiceOrder serviceOrder = dao.getServiceOrder(taskId);
            if (task.getTaskName().equals(TaskName.CONNECT_INSTANCE)) {//connect scenario
                dao.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.ACTIVE);
            }
            else if (task.getTaskName().equals(TaskName.MODIFY_SERVICE)) {//modify scenario
                dao.changeServiceForServiceInstance(taskId, serviceOrder.getServiceInstanceId());
            }
            else if (task.getTaskName().equals(TaskName.DISCONNECT_INSTANCE)) {//disconnect scenario
                dao.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.DISCONNECTED);
            }
            dao.changeTaskStatus(taskId, TaskStatus.COMPLETED);
            dao.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
            dao.setInstanceBlocked(serviceOrder.getServiceInstanceId(), 0);

            response.sendRedirect(CauliflowerInfo.PROVIS_ENGINEER_DASHBOARD_LINK);
            return;
        } catch (SQLException e) {
            logger.error(e);
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
        }
    }
}
