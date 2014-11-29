package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskStatus;
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

@WebServlet(name = "CreateRouterController")
public class CreateRouterController {

    /*
    SOW.6 + CREATE NEXT TASK
    SOW.5
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Task task = (Task) request.getAttribute("task");
        int taskId = task.getTaskId();
        int serviceOrderId = task.getServiceOrderId();

        try {
            //CREATE ROUTER METHOD FROM DAO
            DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
            DAO.INSTANCE.createTaskForProvisioning(serviceOrderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
