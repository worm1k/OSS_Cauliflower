package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.InstanceStatus;
import com.naukma.cauliflower.dao.OrderStatus;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "PortManager")
public class PortManager {

    /*
    SOW.7
    SOW.8
    SOW.10

     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Task task = (Task) request.getAttribute("task");
        /*
        ServiceOrder serviceOrder = DAO.INSTANCE.getServiceOrder(task.getTaskId());
        if (serviceOrder.getOrderScenario().equals(Scenario.NEW.toString())) {
            //TODO
            DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
            DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.ACTIVE);
        } else if (serviceOrder.getOrderScenario().equals(Scenario.DISCONNECT.toString())) {
            //TODO
            DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
            DAO.INSTANCE.changeInstanceStatus(serviceOrder.getServiceInstanceId(), InstanceStatus.DISCONNECTED);
        }
        try {
            DAO.INSTANCE.changeOrderStatus(serviceOrder.getServiceOrderId(), OrderStatus.COMPLETED);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
