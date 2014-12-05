package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Алексей on 29.11.2014.
 */
@WebServlet(name = "GetTasksController")
public class GetTasksController extends HttpServlet {

    /*
    SOW.4
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        List<Task> tasks = null;
        try {
            tasks = DAO.INSTANCE.getFreeAndProcessingTasksByUserRoleId(user.getUserRoleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("tasks", tasks);
        request.getRequestDispatcher("smthing.jsp").forward(request, response);
    }
}
