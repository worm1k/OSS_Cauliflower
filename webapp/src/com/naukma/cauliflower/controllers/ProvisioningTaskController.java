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
        User user = DAO.INSTANCE.getUserByLoginAndPassword("kemi.kondratenko@gmail.com", "kemi");//JUST FOR END TO END
        //User user = (User) request.getSession().getAttribute("user");
        Integer taskId = (Integer) request.getAttribute("taskId");

        if (DAO.INSTANCE.getTaskStatus(taskId) == TaskStatus.PROCESSING) {
            if(user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor_ProvisioningEngineer()) {

                ServiceOrder serviceOrder = DAO.INSTANCE.getServiceOrder(taskId);
                if (serviceOrder.getOrderScenario().equals(Scenario.NEW.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.ACTIVE);
                }
                else if (serviceOrder.getOrderScenario().equals(Scenario.DISCONNECT.toString())) {
                    DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.DISCONNECTED);
                }
                DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
                DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
                DAO.INSTANCE.setInstanceBlocked(serviceOrder.getServiceInstanceId(), 0);

                //JUST FOR END TO END PURPOSES
                response.sendRedirect("login.jsp");
                //END TO END

                //request.getRequestDispatcher("smthing.jsp").forward(request, response);
            } else
                request.getRequestDispatcher("smthing.jsp?created=you%20have%20no%20rihts%20for%20that").forward(request, response);
        } else
            request.getRequestDispatcher("taskalreadycompleted.jsp").forward(request, response);



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request, response);

    }
}
