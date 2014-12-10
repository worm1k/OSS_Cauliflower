package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ProvisioningTaskController")
public class ProvisioningTaskController extends HttpServlet {

    /*
    SOW.7
    SOW.8
    SOW.10
    All operations(creating cable, assigning port, instance or removing these entities is done by installation engineer)
    This one just activates/deactivates service instance
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if (user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }

        //Integer taskId = (Integer) request.getAttribute(CauliflowerInfo.TASK_ID_PARAM);

        try {
            System.out.println("before");
            Integer taskId = Integer.parseInt(request.getParameter(CauliflowerInfo.TASK_ID_PARAM));
            {//help
                System.out.println(taskId);
                System.out.println(DAO.INSTANCE.getTaskStatus(taskId));
                System.out.println(user.getUserRoleId());
                System.out.println(DAO.INSTANCE.getUserRoleIdFor(UserRole.PROVISIONING_ENG));
            }

            if (DAO.INSTANCE.getTaskStatus(taskId) == TaskStatus.PROCESSING &&
                user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor(UserRole.PROVISIONING_ENG)) {

                ServiceOrder serviceOrder = DAO.INSTANCE.getServiceOrder(taskId);
                if (serviceOrder.getOrderScenario().equals(Scenario.NEW.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.ACTIVE);
                }
                else if (serviceOrder.getOrderScenario().equals(Scenario.MODIFY.toString())) {
                    DAO.INSTANCE.changeServiceForServiceInstance(taskId, serviceOrder.getServiceInstanceId());
                }
                else if (serviceOrder.getOrderScenario().equals(Scenario.DISCONNECT.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.DISCONNECTED);
                }
                DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
                DAO.INSTANCE.setInstanceBlocked(serviceOrder.getServiceInstanceId(), 0);

                response.sendRedirect(CauliflowerInfo.PROVIS_ENGINEER_DASHBOARD_LINK);
            } else
                response.sendRedirect(CauliflowerInfo.HOME_LINK);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { }
}
