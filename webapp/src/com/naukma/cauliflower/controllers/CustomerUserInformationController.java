package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.entities.ServiceInstance;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vladmyr
 * Date: 10.12.14
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */

@WebServlet(name = "CustomerUserInformationController")
public class CustomerUserInformationController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = null;
        int customerUserId = 0;

        try{
            user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
            customerUserId = Integer.parseInt(request.getParameter(CauliflowerInfo.USER_ID_ATTRIBUTE));

            final User customerUser = DAO.INSTANCE.getCustomerUserById(customerUserId);
            final List<ServiceInstance> lstServiceInstance = DAO.INSTANCE.getInstances(customerUserId);
            final List<ServiceOrder> lstServiceOrder = DAO.INSTANCE.getOrders(customerUserId);

            request.setAttribute("customerUser", customerUser);
            request.setAttribute("lstServiceInstance", lstServiceInstance);
            request.setAttribute("lstServiceOrder", lstServiceOrder);
            request.getRequestDispatcher(CauliflowerInfo.SUPPORT_ENGINEER_USER_INFORMATION_LINK).forward(request, response);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
