package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.*;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Vladmyr
 * Date: 14.12.14
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(name = "TaskInformationController")
public class TaskInformationController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);

        if(user == null){
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }else if(!user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString()) && !user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString())){
            response.sendRedirect(CauliflowerInfo.HOME_LINK);
        }else{
            try{
                final int taskId = Integer.parseInt(request.getParameter(CauliflowerInfo.TASK_ID_PARAM));
                final Task task = DAO.INSTANCE.getTaskById(taskId);
                final ServiceOrder serviceOrder = DAO.INSTANCE.getServiceOrderById(task.getServiceOrderId());
                final ServiceInstance serviceInstance = DAO.INSTANCE.getInstanceById(serviceOrder.getServiceInstanceId());
                final Service service = DAO.INSTANCE.getServiceById(serviceInstance.getServiceId());
                final Service newService = DAO.INSTANCE.getServiceModifyToByTaskId(taskId);
                final User customerUser = DAO.INSTANCE.getCustomerUserById(serviceInstance.getUserId());

                request.getSession().setAttribute(CauliflowerInfo.TASK_PARAM, task);
                request.getSession().setAttribute(CauliflowerInfo.SERVICE_ORDER_ATTRIBUTE, serviceOrder);
                request.getSession().setAttribute(CauliflowerInfo.SERVICE_INSTANCE_ATTRIBUTE, serviceInstance);
                request.getSession().setAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE, service);
                request.getSession().setAttribute(CauliflowerInfo.MODIFY_TO_SERVICE_ATTRIBUTE, newService);
                request.getSession().setAttribute(CauliflowerInfo.CUSTOMER_USER_ATTRIBUTE, customerUser);

                if(user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString())){
                    request.getSession().setAttribute(CauliflowerInfo.ACTION_ATTRIBUTE, CauliflowerInfo.INSTALL_ENGINEER_CONTROLLER_LINK);
                }else{
                    request.getSession().setAttribute(CauliflowerInfo.ACTION_ATTRIBUTE, CauliflowerInfo.PROVIS_ENGINEER_CONTROLLER_LINK);
                }
            }catch(Exception e){
                e.printStackTrace();
                //add some error messge
            }finally{
                response.sendRedirect(CauliflowerInfo.ENGINEER_DASHBOARD_TASK_LINK);
            }
        }
    }
}
