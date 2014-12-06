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
        System.out.println("getTasks");
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if (user == null) {
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
        List<Task> tasks = null;
        try {
            tasks = DAO.INSTANCE.getFreeAndProcessingTasksByUserRoleId(user.getUserRoleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute(CauliflowerInfo.TASKS_PARAM, tasks);
        request.getRequestDispatcher(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK).forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
