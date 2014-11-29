package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Алексей on 29.11.2014.
 */

@WebServlet(name = "TaskManager")
public class TaskManager {
    /*
    SOW.4
    */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int taskId =  Integer.parseInt(request.getParameter("taskId"));
        String status = request.getParameter("taskStatus");
        if(status.equals("PROCESSING"))
        DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.FREE);
        else if(status.equals("FREE"))
        DAO.INSTANCE.changeTaskStatus(taskId,TaskStatus.PROCESSING);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
