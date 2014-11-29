package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.TaskStatus;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "GetTasksController")
public class GetTasksController {

    /*
    SOW.4
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        //get tasks with taskStatusId TaskStatus.FREE and userRole
        //List<Task> tasks = getTasksByStatusAndRole(TaskStatus.FREE, user.getUserRoleId());
        //request.setAttribute("tasks", tasks);
        //request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
