package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskStatus;
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

@WebServlet(name = "CreateRouterController")
public class CreateRouterController extends HttpServlet {

    /*
    RI.9
    SOW.6 ( RI.1 ) + CREATE NEXT TASK
    SOW.5
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Task task = (Task) request.getAttribute("task");
        int taskId = task.getTaskId();
        int serviceOrderId = task.getServiceOrderId();

        //RI.9
        //The system should allow creating Devices, Ports and Cables only by Installation Engineer
        if(user.getUserRoleId() == DAO.INSTANCE.getUserRoleIdFor_InstallationEngineer()) {
            DAO.INSTANCE.createRouter();
            DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.COMPLETED);
            DAO.INSTANCE.createTaskForProvisioning(serviceOrderId);

            request.getRequestDispatcher("smthing.jsp?created=true").forward(request, response);
        }else request.getRequestDispatcher("smthing.jsp?created=you%20have%20no%20rihts%20for%20that").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
