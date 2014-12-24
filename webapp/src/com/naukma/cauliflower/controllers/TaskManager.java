package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskStatus;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

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

        final User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(user == null || user.isBlocked())
        {
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
        else if(!user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString())
                && !user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString()))
        {
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }
        else
        {
            int taskId =  Integer.parseInt(request.getParameter(CauliflowerInfo.TASK_ID_PARAM));
            String status = request.getParameter(CauliflowerInfo.TASK_STATUS_PARAM);
            try
            {
                if(status.equals(TaskStatus.PROCESSING.toString()))
                    DAO.INSTANCE.changeTaskStatus(taskId, TaskStatus.FREE);
                else if(status.equals(TaskStatus.FREE.toString()))
                    DAO.INSTANCE.changeTaskStatus(taskId,TaskStatus.PROCESSING);
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

            if(user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString()))
            {
                response.sendRedirect(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK);
            }else{
                response.sendRedirect(CauliflowerInfo.PROVIS_ENGINEER_DASHBOARD_LINK);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
    }

}
