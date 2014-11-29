package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    //KaspYar
    /**
     * Get user by its login and password
     * @param login user login
     * @param password user password
     * @return found user or null if user does not exist
     * */
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

    //KaspYar
    /**
     * Creates new service order
     * @param scenario scenario for the order
     * @param idServiceInstance id of service instance for disconnect scenario
     * @return id of created instance
     * @see com.naukma.cauliflower.dao.Scenario
     * */
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
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return 0;

    }



    //KaspYar

    /**
     * Creates new service instance
     * @param userId selected user id
     * @param  serviceLocation location for the instance
     * @param serviceId in of selected service
     * @return id of created instance
     * */

    public int createServiceInstance(int userId, ServiceLocation serviceLocation,
                                        int serviceId)
    {

        //default status PLANNED

        return 1;
    }
    //KaspYar

    /**
     * Creates task for installation engineer for selected service order
     * @param serviceOrderId
     * @return id of created task
     * */
    public int createTaskForInstallation(int serviceOrderId) {
        return 1;

    }
    //KaspYar


    /**
     * Creates task for provisioning engineer for selected service order
     * @param serviceOrderId
     * @return id of created task
     * */
    public int createTaskForProvisioning(int serviceOrderId) {
        return 1;

    }

    //KaspYar

    /**
     * Connects selected instance and selected user
     * @param instanceId id of the instance
     * @param userId id of the user    * */
    public void setUserForInstance(int instanceId,int userId){

    }

    //KaspYar

    /**
     * Connects selected instance and selected order
     * @param instanceId id of the instance
     * @param orderId id of the order    * */
    public void setInstanceForOrder(int instanceId, int orderId){
        
    }

    //KaspYar
    /**
     * Set selected status for selected instance
     * @param instanceId id of the instance
     * @param status status of the instance
     * */
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
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar
    /**
     * Set selected status for selected order
     * @param orderId id of the order
     * @param orderStatus status of the task
     * */
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
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
    }

    //KaspYar
    /**
     * Set selected status for selected task
     * @param taskId id of the task
     * @param taskStatus status of the task
     * */
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
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar
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
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar
    public List<Task> getTasksByStatusAndRole(int taskStatusId, int userRoleId){
        return null;
    }
    //KaspYar

    public List<Service> getServicesByProviderLocationId(int providerLocationId){
        return null;
    }

    //KaspYar
    /**
     *  Creates new router
     * */
    public void createRouter(){
        int amountsOfPorts = 60;
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO ROUTER(ID) VALUES(null)");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID) M FROM ROUTER");
            ResultSet resultSet = preparedStatement.executeQuery();
            int idRouter = 0;
            if(resultSet.next()){
                idRouter = resultSet.getInt("M");
            }
            //!!!!!!!!!!!!VERY       BADDD!!!!!!!!!!!!!!
            preparedStatement = connection.prepareStatement("INSERT INTO PORT(ID_ROUTER) VALUES(?)");
            preparedStatement.setInt(1, idRouter);
            for (int i=0;i<amountsOfPorts;i++){
                preparedStatement.executeUpdate();
            }
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction of creating new router");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar
    /**
     *  Returns ServiceOrder for selected task
     *  @param taskId  Id of the task
     * */

    public ServiceOrder getServiceOrder(int taskId){
        return null;
    }

    //KaspYar
    /**
     *  Returns ArrayList of orders for selected user
     *  @param userId  Id of the user
     * */
    public ArrayList<ServiceOrder> getOrders(int userId){
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();

        return  result;

    }

    //KaspYar
    /**
     *  Returns ArrayList of instances for selected user
     *  @param userId  Id of the user
     * */

    public ArrayList<ServiceInstance> getInstances(int userId){
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                                                            "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.ISACTIVEORDER, " +
                                                            "L.ADRESS,L.LATITUDE, L.LONGITUDE, SIS.NAME " +
                                                            "FROM (SERVICEINSTANCE SI INNER JOIN (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                                                                                        "ON SI.ID_SERVICE_LOCATION = SL.ID)" +
                                                            "INNER JOIN SERVICEINSTANCESTATUS SIS " +
                                                                "ON SI.SERVICE_INSTANCE_STATUS = SIS.ID " +
                                                            "WHERE ID_USER = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                 /*
                * public ServiceInstance(int instanceId, int userId,
                                       int serviceLocationId, String locationAddress,
                                       int locationLongitude, int locationLatitude,
                                       int serviceId, int instanceStatusId,
                                       String instanceStatus, int cableId, boolean isBlocked)
                * */
                result.add(new ServiceInstance(resultSet.getInt("SI.ID"), resultSet.getInt("SI.ID_USER"),
                                resultSet.getInt("SI.ID_SERVICE_LOCATION"), resultSet.getString("L.ADRESS"),
                                resultSet.getInt("L.LONGITUDE"), resultSet.getInt("L.LATITUDE"),
                                resultSet.getInt("SI.ID_SERVICE"), resultSet.getInt("SI.SERVICE_INSTANCE_STATUS"),
                                resultSet.getString("SIS.NAME"), resultSet.getInt("SI.ID_CABLE"), (resultSet.getInt("SI.ISACTIVEORDER")== 1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        result.trimToSize();
        return  result;

    }

    //KaspYar

    /**
     *  Returns ArrayList of all orders
     * */
    public ArrayList<ServiceOrder> getAllOrders(){
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        return  result;

    }
    //KaspYar

    /**
     *  Returns ArrayList of all instances
     * */
    public ArrayList<ServiceInstance> getAllInstances(){
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                                                                "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.ISACTIVEORDER, " +
                                                                "L.ADRESS,L.LATITUDE, L.LONGITUDE, SIS.NAME " +
                                                                "FROM (SERVICEINSTANCE SI INNER JOIN (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                                                                "ON SI.ID_SERVICE_LOCATION = SL.ID)" +
                                                                "INNER JOIN SERVICEINSTANCESTATUS SIS " +
                                                                "ON SI.SERVICE_INSTANCE_STATUS = SIS.ID ");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                 /*
                * public ServiceInstance(int instanceId, int userId,
                                       int serviceLocationId, String locationAddress,
                                       int locationLongitude, int locationLatitude,
                                       int serviceId, int instanceStatusId,
                                       String instanceStatus, int cableId, boolean isBlocked)
                * */
                result.add(new ServiceInstance(resultSet.getInt("SI.ID"), resultSet.getInt("SI.ID_USER"),
                        resultSet.getInt("SI.ID_SERVICE_LOCATION"), resultSet.getString("L.ADRESS"),
                        resultSet.getInt("L.LONGITUDE"), resultSet.getInt("L.LATITUDE"),
                        resultSet.getInt("SI.ID_SERVICE"), resultSet.getInt("SI.SERVICE_INSTANCE_STATUS"),
                        resultSet.getString("SIS.NAME"), resultSet.getInt("SI.ID_CABLE"), (resultSet.getInt("SI.ISACTIVEORDER")== 1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        result.trimToSize();
        return  result;

    }


    public ResultSet reportTester() throws SQLException
    {
        Connection connection = getConnection();
        preparedStatement = connection.prepareStatement("SELECT * FROM USERS");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    //KaspYar
    public List<ProviderLocation> getProviderLocations(){
        ArrayList<ProviderLocation> result = new ArrayList<ProviderLocation>();
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT * " +
                                                            "FROM PROVIDERLOCATION PL INNER JOIN LOCATION L ON PL.ID_LOCATION = L.ID");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //public ProviderLocation(int providerLocationId, String locationAddress, int locationLongitude, int locationLatitude)
                result.add(new ProviderLocation(resultSet.getInt("ID"), resultSet.getString("ADRESS"),
                                                resultSet.getInt("LONGITUDE"), resultSet.getInt("LATITUDE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

        return result;
    }

    //KaspYar
    public List<Service> getServices(){
        return null;
    }



    //KaspYar
    /**
     * Creates service location record in database from ServiceLocation object
     *  @param  serviceLocation ServiceLocation object to write
     *  @see com.naukma.cauliflower.entities.ServiceLocation
     * */
    public int createServiceLocation(ServiceLocation serviceLocation){
            return 1;

    }

 }


