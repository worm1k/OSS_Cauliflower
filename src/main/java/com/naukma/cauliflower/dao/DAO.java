package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ldap.PagedResultsControl;
import javax.smartcardio.CommandAPDU;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public enum DAO {
    INSTANCE;
    // assumes the current class is called logger
    private final Logger logger = Logger.getLogger(DAO.class.getName());
    private DataSource dataSource;
    private PreparedStatement preparedStatement;

    private DAO() {
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("jdbc/oraclesource");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByLoginAndPassword(String login, String password) {
        Connection connection = getConnection();
        User user = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * " +
                    "FROM USERS U INNER JOIN USERROLE UR ON U.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? AND PASSWORD = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUser = resultSet.getInt("ID_USER");
                int idUserrole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String firstName = resultSet.getString("F_NAME");
                String lastName = resultSet.getString("L_NAME");
                String phone = resultSet.getString("PHONE");
                String userrole = resultSet.getString("NAME");
                user = new User(idUser, idUserrole, userrole, eMail, firstName, lastName, phone);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return user;
    }


    public int createServiceOrder(Scenario scenario, Integer idServiceInstance) {
        //default status ENTERING
        OrderStatus orderStatus = OrderStatus.ENTERING;
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT ID_ORDERSCENARIO FROM ORDERSCENARIO WHERE NAME = ?");
            preparedStatement.setString(1,scenario.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int idOrderScenario = 0;
            if (resultSet.next()){
                idOrderScenario = resultSet.getInt("ID_ORDERSCENARIO");
            }

            preparedStatement = connection.prepareStatement("SELECT ID_ORDERSTATUS FROM ORDERSTATUS WHERE NAME = ?");
            preparedStatement.setString(1, orderStatus.toString());
            int idOrderStatus = 0;
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                idOrderStatus = resultSet.getInt("ID_ORDERSTATUS");
            }
            if (idServiceInstance == null){
                preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_ORDERSCENARIO,ID_ORDERSTATUS) " +
                        "VALUES(?,? )");
                preparedStatement.setInt(1, idOrderScenario);
                preparedStatement.setInt(2, idOrderStatus);
            }else{
                preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_SERVICEINSTANCE, ID_ORDERSCENARIO,ID_ORDERSTATUS) " +
                        "VALUES(?, ?,? )");
                preparedStatement.setInt(1, idServiceInstance.intValue());
                preparedStatement.setInt(2, idOrderScenario);
                preparedStatement.setInt(3, idOrderStatus);
            }

            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID_SERVICEORDER) RES FROM SERVICEORDER");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("RES");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;

    }




    public int createServiceInstance(int userId, ServiceLocation serviceLocation,
                                        int serviceId)
    {

        //default status PLANNED

        return 1;
    }

    public int createTaskForInstallation(int serviceOrderId) {
        return 1;

    }

    public int createTaskForProvisioning(int serviceOrderId) {
        return 1;

    }


    public void setUserForInstance(int instanceId,int userId){

    }


    public void setInstanceForOrder(int instanceId, int orderId){



    }

    public void changeInstanceStatus(int instanceId, InstanceStatus status) {
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                                                            "SET SERVICE_INSTANCE_STATUS = (SELECT ID " +
                                                                                           "FROM SERVICEINSTANCESTATUS " +
                                                                                            "WHERE NAME = ?) " +
                                                            "WHERE ID = ?");

            preparedStatement.setString(1, status.toString());
            preparedStatement.setInt(2, instanceId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void changeOrderStatus(int orderId, OrderStatus orderStatus) {
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEORDER " +
                                                            "SET ID_ORDERSTATUS = (SELECT ID_ORDERSTATUS " +
                                                                                    "FROM ORDERSTATUS " +
                                                                                    "WHERE NAME = ?) " +
                                                            "WHERE ID_SERVICEORDER = ?");

            preparedStatement.setString(1, orderStatus.toString());
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeTaskStatus(int taskId, TaskStatus taskStatus) {
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE TASK " +
                                                            "SET ID_TASKSTATUS = (SELECT ID_TASKSTATUS " +
                                                                                "FROM TASKSTATUS " +
                                                                                "WHERE NAME = ?) " +
                                                            "WHERE ID_TASK = ?");

            preparedStatement.setString(1, taskStatus.toString());
            preparedStatement.setInt(2, taskId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void setInstanceBlocked(int instanceId, int isBlocked){
        //int isBlocked = 1;
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                    "SET ISACTIVEORDER = ?"+
                    "WHERE ID = ?");

            preparedStatement.setInt(1, isBlocked);
            preparedStatement.setInt(2, instanceId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public List<Task> getTasksByStatusAndRole(int taskStatusId, int userRoleId){
        return null;
    }

    public List<Service> getServicesByProviderLocationId(int providerLocationId){
        return null;
    }

    public void createRouter(){

    }

    public ServiceOrder getServiceOrder(int taskId){
        return null;
    }
    public ArrayList<ServiceOrder> getOrders(int userId){
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();

        return  result;

    }

    public ArrayList<ServiceInstance> getInstances(int userId){
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM SERVICEINSTANCE WHERE ID_USER = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //result.add(new ServiceInstance());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return  result;

    }


    public ArrayList<ServiceOrder> getAllOrders(){
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        return  result;

    }

    public ArrayList<ServiceInstance> getAllInstances(){
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        return  result;

    }


    public ResultSet reportTester() throws SQLException
    {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM USERS");
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }
}


