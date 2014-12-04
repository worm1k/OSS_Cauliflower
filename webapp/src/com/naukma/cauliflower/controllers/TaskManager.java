package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Алексей on 29.11.2014.
 */

@WebServlet(name = "TaskManager")
public class TaskManager extends HttpServlet {
    /*
    SOW.4
    */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int taskId =  Integer.parseInt(request.getParameter("taskId"));
        String status = request.getParameter("taskStatus");
        try {
            if(status.equals(TaskStatus.PROCESSING.toString()))
                    DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.FREE);
            else if(status.equals(TaskStatus.FREE.toString()))
            DAO.INSTANCE.changeTaskStatus(taskId,TaskStatus.PROCESSING);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }
}
