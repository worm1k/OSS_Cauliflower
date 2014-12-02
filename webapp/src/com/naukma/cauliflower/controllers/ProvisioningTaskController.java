package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "PortManager")
public class ProvisioningTaskController extends HttpServlet {

    /*
    SOW.7
    SOW.8
    SOW.10
    All operations(creating cable, assigning port, instance or removing these entities is done by installation engineer)
    This one just activates/deactivates service instance
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Task task = (Task) request.getAttribute("task");
        int taskId = task.getTaskId();

        if (DAO.INSTANCE.getTaskStatus(taskId) == TaskStatus.PROCESSING) {
            if(user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor_ProvisioningEngineer()) {

                ServiceOrder serviceOrder = DAO.INSTANCE.getServiceOrder(task.getTaskId());
                if (serviceOrder.getOrderScenario().equals(Scenario.NEW.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.ACTIVE);
                }
                else if (serviceOrder.getOrderScenario().equals(Scenario.DISCONNECT.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.DISCONNECTED);
                }
                DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
                DAO.INSTANCE.setInstanceBlocked(serviceOrder.getServiceInstanceId(), 0);
                request.getRequestDispatcher("smthing.jsp").forward(request, response);

            } else
                request.getRequestDispatcher("smthing.jsp?created=you%20have%20no%20rihts%20for%20that").forward(request, response);
        } else
            request.getRequestDispatcher("taskalreadycompleted.jsp").forward(request, response);



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
