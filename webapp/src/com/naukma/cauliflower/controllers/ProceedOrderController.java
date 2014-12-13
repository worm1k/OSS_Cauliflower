package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.*;
import com.naukma.cauliflower.entities.Service;
import com.naukma.cauliflower.entities.ServiceLocation;
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
 * Created by Max on 29.11.2014.
 */
@WebServlet(name = "ProceedOrderController")
public class ProceedOrderController extends HttpServlet {
    private User user;
    private int orderId = -1;
    private int serviceInstanceId = -1;
    private int taskId = -1;

    /*
    ACK.1  ACK.2(OPTIONAL)
    ACK.3
    ACK.4
    ACK.10
    ACK.12
    SOW1
    SOW2
    SOW3
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       // System.out.println("IN PROCEED ORDER");
        user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        String scenario = request.getParameter(CauliflowerInfo.SCENARIO_PARAM);
       // scenario = "NEW";
        if(user == null) {
            response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        }
        try {
            if(scenario==null || scenario.equals(Scenario.NEW.toString())){
                scenarioNew(request);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            }else if (scenario.equals(Scenario.DISCONNECT.toString())){
                scenarioDisconnect(request,response);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            }else if (scenario.equals(Scenario.MODIFY.toString())){
                scenarioModify(request,response);
                response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
            }
        }
        catch (SQLException e) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
         //   response.sendRedirect(pathFrom);
        }

        return;

    }

    private void scenarioNew(HttpServletRequest request) throws SQLException {
        createNewOrder();
        changeOrderStatus();
        createServiceInstance(request);
        connectInstanceWithOrder();
        setInstanceBlocked();
        int availablePorts = DAO.INSTANCE.getFreePortsNum() + DAO.INSTANCE.getTasksNumByName(TaskName.CREATE_NEW_ROUTER) * CauliflowerInfo.PORTS_QUANTITY ;
        int neededPorts =  DAO.INSTANCE.getTasksNumByName(TaskName.CREATE_CIRCUIT) + DAO.INSTANCE.getTasksNumByStatus(TaskStatus.WAITING);
                ;
        //getFreePorts() + DAO.INSTANCE.getTasksByStatusAndRole(TaskStatus.WAITING,UserRole.INSTALLATION_ENG).size();

        if(availablePorts >= neededPorts )
        taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.INSTALLATION_ENG,TaskName.CONNECT_NEW_PERSON,TaskStatus.FREE);
        else{
         DAO.INSTANCE.createNewTask(orderId,UserRole.INSTALLATION_ENG,TaskName.CREATE_NEW_ROUTER,TaskStatus.FREE);
        taskId = DAO.INSTANCE.createNewTask(orderId,UserRole.INSTALLATION_ENG,TaskName.CONNECT_NEW_PERSON,TaskStatus.WAITING);

        }
    }

    private void scenarioModify(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        boolean blocked = DAO.INSTANCE.isInstanceBlocked(serviceInstanceId);
        boolean disconnected = DAO.INSTANCE.isInstanceDisconnected(serviceInstanceId);
        if(blocked){ //actually it is blocked
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_BLOCKED_ERROR_MESSAGE);
            //request.getRequestDispatcher(CauliflowerInfo.DASHBOARD_LINK);
        }else if(disconnected){
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_DISCONNECTED_ERROR_MESSAGE);
        }
        else{ //it is not blocked
            createModifyOrder(serviceInstanceId);
            changeOrderStatus();
            setInstanceBlocked();
            taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.PROVISIONING_ENG,TaskName.MODIFY_SERVICE,TaskStatus.FREE);
            setNewServiceForTask(request);
        }

    }

    private void scenarioDisconnect(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        boolean blocked = DAO.INSTANCE.isInstanceBlocked(serviceInstanceId);
        boolean disconnected = DAO.INSTANCE.isInstanceDisconnected(serviceInstanceId);

       // checkBlocked(request, response);
       // checkDisconnected(request,response);
        if(blocked){
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_BLOCKED_ERROR_MESSAGE);

        }else if(disconnected){
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_DISCONNECTED_ERROR_MESSAGE);
         //   response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        }
        else {
            createDisconnectOrder(serviceInstanceId);
            changeOrderStatus();
            setInstanceBlocked();
            taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.INSTALLATION_ENG, TaskName.BREAK_CIRCUIT,TaskStatus.FREE);
        }
    }

    private void checkBlocked(HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {
        serviceInstanceId =  Integer.parseInt(request.getParameter(CauliflowerInfo.INSTANCE_ID_PARAM));
        boolean blocked = DAO.INSTANCE.isInstanceBlocked(serviceInstanceId);
      //vladmyr
        if(blocked){
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_BLOCKED_ERROR_MESSAGE);
        }else{
            createDisconnectOrder(serviceInstanceId);
            changeOrderStatus();
            setInstanceBlocked();
            taskId = DAO.INSTANCE.createNewTask(orderId, UserRole.INSTALLATION_ENG,TaskName.BREAK_CIRCUIT,TaskStatus.FREE);
        }

    }



    private void checkDisconnected(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        boolean disconnected = DAO.INSTANCE.isInstanceDisconnected(serviceInstanceId);
        if (!disconnected) {
            request.getSession().setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.INSTANCE_IS_DISCONNECTED_ERROR_MESSAGE);
            response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        }
    }

    private void createNewOrder() throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.NEW,null);
    }

    private void createDisconnectOrder(Integer instanceId) throws SQLException
    {
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.DISCONNECT, instanceId);
    }

    private void createModifyOrder(Integer instanceId)throws SQLException{
        orderId = DAO.INSTANCE.createServiceOrder(user.getUserId(),Scenario.MODIFY, instanceId);
    }

    private void setNewServiceForTask(HttpServletRequest request) throws SQLException{
        int serviceId = Integer.parseInt(request.getParameter(CauliflowerInfo.SERVICE_ID_PARAM));
        DAO.INSTANCE.setServiceForTask(taskId,serviceId);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);


    }

    private void changeOrderStatus() throws SQLException
    {
        DAO.INSTANCE.changeOrderStatus(orderId,OrderStatus.PROCESSING);
    }

    private void createServiceInstance(HttpServletRequest request) throws SQLException {
        ServiceLocation serviceLocation = (ServiceLocation)request.getSession().getAttribute(
                CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);
        Service service = (Service)request.getSession().getAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        serviceLocation.setServiceLocationId(DAO.INSTANCE.createServiceLocation(serviceLocation));
        serviceInstanceId = DAO.INSTANCE.createServiceInstance(user.getUserId(),serviceLocation,service.getServiceId());
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_LOCATION_ATTRIBUTE);

    }


    private void connectInstanceWithOrder() throws SQLException
    {
        DAO.INSTANCE.setInstanceForOrder(serviceInstanceId,orderId);
    }

    private void setInstanceBlocked() throws SQLException
    {
            DAO.INSTANCE.setInstanceBlocked(serviceInstanceId,1);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    }


}
