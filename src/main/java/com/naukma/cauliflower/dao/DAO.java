package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public enum DAO {
    INSTANCE;
    // assumes the current class is called logger
    private static final Logger logger = Logger.getLogger(DAO.class);
    private DataSource dataSource;
    private static final String BD_JNDI = "jdbc/oraclesource"; // no magic numbers

    private DAO() {
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(BD_JNDI);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("com/naukma/cauliflower/properties/log4j.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(props);

    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void close(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        connection.setAutoCommit(true);
        if (!preparedStatement.isClosed()) preparedStatement.close();
        if (!connection.isClosed()) connection.close();
    }


/**---------------------------------------------------------------------HALYA---------------------------------------------------------------------**/
    //Galya_Sh
    //просто отримуємо айди юзер ролі яка є Installation Engineer
    public int getUserRoleIdFor_InstallationEngineer() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 4;
        try {
            preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = 'INSTALLATION_ENG'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("RES");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return result;
    }
    //Halya
    //if error - return < 0
    //else return id of created user
    public int createUser(User us,String password){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = -1;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO USERS (ID_USERROLE,E_MAIl,PASSWORD,F_NAME,L_Name,PHONE)" +
                                                           "VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1,us.getUserRoleId());
            preparedStatement.setString(2, us.getEmail());
            preparedStatement.setString(3,password);
            preparedStatement.setString(4,us.getFirstName());
            preparedStatement.setString(5,us.getLastName());
            preparedStatement.setString(6,us.getPhone());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID_USER) MAX_ID FROM USERS");
            //int idU = 0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("MAX_ID");
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                //connection.setAutoCommit(true);
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return result;
    }

    //Halya
    //true, if no user with this email
    public boolean checkForEmailUniq(String email){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE E_Mail = ?");
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()){
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 0) {
                result = true;
            } else {
                result = false;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }

    //Halya
    //true, if user with this id exist
    public boolean checkForExistingUserById(int id){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE Id_User = ?");
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()){
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 1) {
                result = true;
            } else {
                result = false;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }

    //Halya
    //if error, return null
    //return blocked user
    //TODO add isBlocked
    public User blockUserById(int idForBlock){
        Connection connection = getConnection();
            User resultUser = null;
            PreparedStatement preparedStatement = null;
            try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE USERS SET Isblocked = 1 WHERE Id_User = ? ");
            preparedStatement.setInt(1, idForBlock);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("ID USER: "+idForBlock+" IS BLOCKED");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM USERS US "+
                                                            "INNER JOIN USERROLE UR ON US.ID_USERROLE = UR.ID_USERROLE "+
                                                            "WHERE ID_USER = ? ");
            preparedStatement.setInt(1,idForBlock);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUserRole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String fName = resultSet.getString("F_Name");
                String lName = resultSet.getString("L_Name");
                String phone = resultSet.getString("PHONE");
                String nameUR = resultSet.getString("NAME");

                resultUser = new User(idForBlock,idUserRole,nameUR,eMail,fName,lName,phone);
            }
            resultSet.close();
            connection.commit();
           // result = idForBlock;

        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                //connection.setAutoCommit(true);
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

        return resultUser;
    }

    //Halya
    //return name of userRole or null, if no userRole with this id
    public String getUserRoleNameByUserRoleId (int userRoleId){
        Connection connection = getConnection();
        String result = null;
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection.prepareStatement("SELECT NAME RES FROM USERROLE WHERE ID_USERROLE = ?");
            preparedStatement.setInt(1,userRoleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("RES");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        //if(userRoleId==1)return "CUSTOMER";
        //if(userRoleId==2)return "Customer Support Engineer";
        //if(userRoleId==3)return "Provisioning Engineer";
        //if(userRoleId==4)return "Installation Engineer";
        return result;
    }

    //Halya
    //return user, if password has been change successful ,
    //else return null
    //TODO add isBlocked
    public User changeUserPasswordById (int userId, String newPassword){
        Connection connection = getConnection();
        User resultUser = null;
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE USERS SET PASSWORD = ? WHERE Id_USER = ?");
            preparedStatement.setString(1,newPassword);
            preparedStatement.setInt(2,userId);
            preparedStatement.executeUpdate();
            {//help
                System.out.println(" FOR ID USER: "+userId+" password was successfully changed");
            }

            preparedStatement = connection.prepareStatement("SELECT *  FROM USERS US "+
                    "INNER JOIN USERROLE UR ON US.ID_USERROLE = UR.ID_USERROLE "+
                    "WHERE ID_USER = ? ");
            preparedStatement.setInt(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUserRole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String fName = resultSet.getString("F_Name");
                String lName = resultSet.getString("L_Name");
                String phone = resultSet.getString("PHONE");
                String nameUR = resultSet.getString("NAME");

                resultUser = new User(userId,idUserRole,nameUR,eMail,fName,lName,phone);
            }
            resultSet.close();
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                //connection.setAutoCommit(true);
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return resultUser;
    }
    //Galya_Sh
    //просто отримуємо айди юзер ролі яка є Provisioning Engineer
    public int getUserRoleIdFor_ProvisioningEngineer() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 0;
        try {
            preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = 'PROVISIONING_ENG'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result =  resultSet.getInt("RES");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return result;
    }

    //Galya_Sh RI.1
    //The system should document Devices.
    // повертаємо просто всю інформацію для репорту (ROUTER, FREE PORTS, OCCUPIED PORTS)
    public ResultSet getDevicesForReport() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT r.id ROUTER, Count(P.Used) FREE, 60 - Count(p.Used) OCCUPIED "+
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) "+
                    "WHERE P.Used  = 0 "+
                    "GROUP BY r.id ");
             resultSet = preparedStatement.executeQuery();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return resultSet;
    }

    //Galya_Sh RI.2
    //The system should document the Ports.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getPortsForReport()  throws SQLException
    {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT * FROM PORT");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //Galya_Sh RI.4
    //The system should document physical link to end user as Cable.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getCablesForReport()  throws SQLException
    {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT * FROM CABLE");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //Galya_Sh RI.5
    //The system should document logical entity of provided Service as Circuit.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getCircuitsForReport() throws SQLException {
        return null;
    }
    //Galya_Sh RI.6
    //Получить ServiceInstance по OrderId. По cable_id получить привязанный порт и сделать его свободным. cable_id в ServiceInstance
    //сделать равным null. Сам кабель удалить из базы.
    //The system should allow deleting of Cables and Circuits.
    public void removeCableFromServiceInstanceAndFreePort(int serviceOrderId) {

    }

/**---------------------------------------------------------------------END HALYA---------------------------------------------------------------------**/

/**---------------------------------------------------------------------KASPYAR---------------------------------------------------------------------**/
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
        PreparedStatement preparedStatement = null;
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
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
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
     *
     * @param userId id of the user to create SO
     * @param scenario scenario for the order
     * @param idServiceInstance id of service instance for disconnect scenario
     * @param calendar service order creation date
     * @return id of created instance
     * @see com.naukma.cauliflower.dao.Scenario
     * */
    public int createServiceOrder(int userId,Scenario scenario,GregorianCalendar calendar, Integer idServiceInstance) {
        //default status ENTERING
        System.out.println("CREATE NEW ORDER!");
        OrderStatus orderStatus = OrderStatus.ENTERING;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("SELECT ID_ORDERSCENARIO FROM ORDERSCENARIO WHERE NAME = ?");
            preparedStatement.setString(1,scenario.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int idOrderScenario = 0;
            if (resultSet.next()){
                idOrderScenario = resultSet.getInt("ID_ORDERSCENARIO");
            }
            {//help
                System.out.println("Scenario : "+ scenario);
                System.out.println("idScenario: " + idOrderScenario);
            }

            preparedStatement = connection.prepareStatement("SELECT ID_ORDERSTATUS FROM ORDERSTATUS WHERE NAME = ?");
            preparedStatement.setString(1, orderStatus.toString());
            int idOrderStatus = 0;
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                idOrderStatus = resultSet.getInt("ID_ORDERSTATUS");
            }
            {//help
                System.out.println("idOrderStatus: "+ idOrderStatus);
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date d = gregorianCalendar.getTime();
            if (idServiceInstance == null){
                preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                        "VALUES(?,?,?,? )");
                preparedStatement.setInt(1, idOrderScenario);
                preparedStatement.setInt(2, idOrderStatus);
                preparedStatement.setDate(3, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
                preparedStatement.setInt(4, userId);
                {//help
                    System.out.println("NULL");
                }
            }else{
                preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_SERVICEINSTANCE, ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                        "VALUES(?, ?,? ,?,?)");
                preparedStatement.setInt(1, idServiceInstance.intValue());
                preparedStatement.setInt(2, idOrderScenario);
                preparedStatement.setInt(3, idOrderStatus);
                preparedStatement.setDate(4, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
                preparedStatement.setInt(5, userId);
                {//help
                    System.out.println("NOT NULL");
                    System.out.println("idServiceInstance "+ idServiceInstance.intValue());
                }
            }

            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID_SERVICEORDER) RES FROM SERVICEORDER");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                {//help
                    System.out.println("RETURN : "+ resultSet.getInt("RES"));
                }
                return resultSet.getInt("RES");
            }
            connection.commit();


        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new router");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //connection.setAutoCommit(true);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();

            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return 0;

    }

    //KaspYar

    /**
     * Connects selected instance and selected user
     * @param instanceId id of the instance
     * @param userId id of the user    * */
    public void setUserForInstance(int instanceId,int userId){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                                                            "SET ID_USER = ? " +
                                                            "WHERE ID = ?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, instanceId);
            {//help
                System.out.println("instanceId: "+ instanceId);
                System.out.println("userId: "+userId);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
    }

    //KaspYar
    /**
     * Set selected status for selected instance
     * @param instanceId id of the instance
     * @param status status of the instance
     * */
    public void changeInstanceStatus(int instanceId, InstanceStatus status) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                                                            "SET SERVICE_INSTANCE_STATUS = (SELECT ID " +
                                                                                           "FROM SERVICEINSTANCESTATUS " +
                                                                                            "WHERE NAME = ?) " +
                                                            "WHERE ID = ?");

            preparedStatement.setString(1, status.toString());
            preparedStatement.setInt(2, instanceId);
            {//help
                System.out.println("InstanceId: "+instanceId);
                System.out.println("Status : " +status.toString() );
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
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
        System.out.println("CHANGE ORDER STATUS");
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEORDER " +
                                                            "SET ID_ORDERSTATUS = (SELECT ID_ORDERSTATUS " +
                                                                                    "FROM ORDERSTATUS " +
                                                                                    "WHERE NAME = ?) " +
                                                            "WHERE ID_SERVICEORDER = ?");

            preparedStatement.setString(1, orderStatus.toString());
            preparedStatement.setInt(2, orderId);
            {//help
                System.out.println("orderId: "+orderId);
                System.out.println("orderStatus: "+orderStatus.toString());
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
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
        PreparedStatement preparedStatement = null;
        {//help
            System.out.println("CHANGE TASK STATUS");
        }
        try {
            preparedStatement = connection.prepareStatement("UPDATE TASK " +
                                                            "SET ID_TASKSTATUS = (SELECT ID_TASKSTATUS " +
                                                                                "FROM TASKSTATUS " +
                                                                                "WHERE NAME = ?) " +
                                                            "WHERE ID_TASK = ?");

            preparedStatement.setString(1, taskStatus.toString());
            preparedStatement.setInt(2, taskId);
            {//help
                System.out.println("taskId: "+ taskId);
                System.out.println("taskStatus: "+ taskStatus.toString());
            }
            preparedStatement.executeUpdate();
            {//help
                System.out.println("SUCCESS!!! CHANGE TASK STATUS");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar

    /**
     * Block instance when exist active task on it
     * @param instanceId id of the instance
     * @param isBlocked 0 - set instance not blocked, 1 - set instance blocked
     */
    public void setInstanceBlocked(int instanceId, int isBlocked){
        //int isBlocked = 1;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        System.out.println("SET INSTANCE BLOCKED!");
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                    "SET HAS_ACTIVE_TASK = ? "+
                    "WHERE ID = ?");

            preparedStatement.setInt(1, isBlocked);
            preparedStatement.setInt(2, instanceId);
            {//help
                System.out.println("instanceId: "+instanceId);
                System.out.println("blocked: "+ isBlocked);
            }
            preparedStatement.executeUpdate();
            System.out.println("SUCCESS! SET INSTANCE BLOCKED!");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

    }

    //KaspYar

    /**
     * returns List<Task>
     * @param taskStatusId id of TaskStatus
     * @param userRoleId id of UserRole
     * @return
     */
    public List<Task> getTasksByStatusAndRole(int taskStatusId, int userRoleId){
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_USERROLE, T.ID_SERVICEORDER, T.ID_TASKSTATUS, TS.NAME TS_NAME, T.NAME T_NAME " +
                                                            "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                                                            "WHERE T.ID_TASKSTATUS = ? AND T.ID_USERROLE = ?");
            preparedStatement.setInt(1, taskStatusId);
            preparedStatement.setInt(2, userRoleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                result.add(new Task(resultSet.getInt("ID_TASK"), resultSet.getInt("ID_USERROLE"), resultSet.getInt("ID_SERVICEORDER"),
                                    resultSet.getInt("ID_TASKSTATUS"), resultSet.getString("TS_NAME"),
                                    TaskName.valueOf(resultSet.getString("T_NAME"))));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return result;
    }
    //KaspYar

    public List<Service> getServicesByProviderLocationId(int providerLocationId){
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                                                                                //"ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID " +
                                                                                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                                                                                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                                                                                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID " +
                                                                                "WHERE S.ID_PROVIDER_LOCATION = ? ");
            preparedStatement.setInt(1, providerLocationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                result.add(new Service(resultSet.getInt("ID_SERVICE_TYPE"), resultSet.getString("ADRESS"), resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE"), resultSet.getString("NAME"), resultSet.getString("SPEED"),
                        resultSet.getInt("ID_PROVIDER_LOCATION"), resultSet.getInt("ID"), resultSet.getDouble("PRICE")));
                        //resultSet.getInt("S.ID_PROVIDER_LOCATION"), resultSet.getInt("S.ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return result;
    }

    //KaspYar
    /**
     *  Creates new router
     * */
    public void createRouter(){
        int amountsOfPorts = 60;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
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
            StringBuilder sb = new StringBuilder("INSERT ALL ");
            for(int i=1;i<=amountsOfPorts;i++) sb.append("INTO PORT(ID_ROUTER) VALUES("+idRouter+") ");
            sb.append("SELECT * FROM DUAL");

            preparedStatement = connection.prepareStatement(sb.toString());
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
                    logger.error("ROLLBACK transaction Failed of creating new router");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
                //connection.setAutoCommit(true);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();

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
        ServiceOrder result= null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OST.NAME OST_NAME, " +
                                                            "SO.ID_SERVICEINSTANCE, SO.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                                                            "FROM (((TASK T INNER JOIN SERVICEORDER SO ON T.ID_SERVICEORDER = SO.ID_SERVICEORDER) " +
                                                            "INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS " +
                                                            ") INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
                                                            "WHERE T.ID_TASK = ?");
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                //ServiceOrder(int serviceOrderId, int orderStatusId,
                                // String orderStatus, int serviceInstanceId,
                                // int orderScenarioId, String orderScenario, GregorianCalendar calendar, int userId)
                                //calendar.set(year, month, day);
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                Date date = resultSet.getDate("OUR_DATE");
                gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
                result = new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"), resultSet.getInt("ID_ORDERSTATUS"), resultSet.getString("OST_NAME"),
                                            resultSet.getInt("ID_SERVICEINSTANCE"), resultSet.getInt("ID_ORDERSCENARIO"), resultSet.getString("OSC_NAME"),
                                            gregorianCalendar, resultSet.getInt("ID_USER"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
//                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

        return result;
    }




    //KaspYar
    /**
     * Connects selected instance and selected order
     * @param instanceId id of the instance
     * @param orderId id of the order    * */
    public void setInstanceForOrder(int instanceId, int orderId){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        System.out.println("SET INSTANCE FOR ORDER");
        try {
            preparedStatement = connection.prepareStatement("UPDATE SERVICEORDER " +
                                                            "SET ID_SRVICEINSTANCE = ? " +
                                                            "WHERE ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, instanceId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
    }




    //KaspYar
    /**
     *  Returns ArrayList of orders for selected user
     *  @param userId  Id of the user
     * */

    //trouble in our database: user is not connected with ServiceOrder scenario NEW FIXED!
     public ArrayList<ServiceOrder> getOrders(int userId){
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        Connection connection = getConnection();
         PreparedStatement preparedStatement = null;
        try {
            preparedStatement  = connection.prepareStatement("SELECT * FROM SERVICEORDER " +
                                                                "WHERE ID_USER = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //ServiceOrder(int serviceOrderId, int orderStatusId,
                // String orderStatus, int serviceInstanceId,
                // int orderScenarioId, String orderScenario)

                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                Date date = resultSet.getDate("OUR_DATE");
                gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
                result.add(new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"), resultSet.getInt("ID_ORDERSTATUS"), resultSet.getString("OST_NAME"),
                        resultSet.getInt("ID_SERVICEINSTANCE"), resultSet.getInt("ID_ORDERSCENARIO"), resultSet.getString("OSC_NAME"),
                        gregorianCalendar, resultSet.getInt("ID_USER")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                preparedStatement.close();
//                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
         result.trimToSize();
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
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                                                            "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.HAS_ACTIVE_TASK, " +
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
                result.add(new ServiceInstance(resultSet.getInt("ID"), resultSet.getInt("ID_USER"),
                                resultSet.getInt("ID_SERVICE_LOCATION"), resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"), resultSet.getDouble("LATITUDE"),
                                resultSet.getInt("ID_SERVICE"), resultSet.getInt("SERVICE_INSTANCE_STATUS"),
                                resultSet.getString("NAME"), resultSet.getInt("ID_CABLE"), (resultSet.getInt("HAS_ACTIVE_TASK")== 1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                close(connection, preparedStatement);
//                preparedStatement.close();
//                connection.close();
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OS_NAME, " +
                                                            "SO.ID_SERVICEINSTANCE, OSC.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                                                            "FROM (SERVICEORDER SO INNER JOIN  ORDERSTATUS OS ON SO.ID_ORDERSTATUS = OS.ID_ORDERSTATUS) " +
                                                            "INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSTATUS ");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //public ServiceOrder(int serviceOrderId, int orderStatusId, String orderStatus,
                //                     int serviceInstanceId, int orderScenarioId, String orderScenario)

                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                Date date = resultSet.getDate("OUR_DATE");
                gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
                result.add(new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"), resultSet.getInt("ID_ORDERSTATUS"),
                                            resultSet.getString("OS_NAME"), resultSet.getInt("ID_SERVICEINSTANCE"),
                                            resultSet.getInt("ID_ORDERSCENARIO"), resultSet.getString("OSC_NAME"),
                                            gregorianCalendar, resultSet.getInt("ID_USER")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                close(connection, preparedStatement);
//                preparedStatement.close();
//                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return  result;

    }
    //KaspYar

    /**
     *  Returns ArrayList of all instances
     * */
    public ArrayList<ServiceInstance> getAllInstances(){
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                                                                "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.HAS_ACTIVE_TASK, " +
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
                result.add(new ServiceInstance(resultSet.getInt("ID"), resultSet.getInt("ID_USER"),
                        resultSet.getInt("ID_SERVICE_LOCATION"), resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"), resultSet.getDouble("LATITUDE"),
                        resultSet.getInt("ID_SERVICE"), resultSet.getInt("SERVICE_INSTANCE_STATUS"),
                        resultSet.getString("NAME"), resultSet.getInt("ID_CABLE"), (resultSet.getInt("HAS_ACTIVE_TASK")== 1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                close(connection, preparedStatement);
//                preparedStatement.close();
//                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        result.trimToSize();
        return  result;

    }


    public ResultSet reportTester() throws SQLException
    {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT * FROM USERS");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    //KaspYar
    public List<ProviderLocation> getProviderLocations(){
        ArrayList<ProviderLocation> result = new ArrayList<ProviderLocation>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * " +
                                                            "FROM PROVIDERLOCATION PL INNER JOIN LOCATION L ON PL.ID_LOCATION = L.ID");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //public ProviderLocation(int providerLocationId, String locationAddress, int locationLongitude, int locationLatitude)
                result.add(new ProviderLocation(resultSet.getInt("ID"), resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"), resultSet.getDouble("LATITUDE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * return List<Services> of all Services
     * @return
     */
    public List<Service> getServices(){
        //public Service(int serviceTypeId, String locationAddress,
        //              int locationLongitude, int locationLatitude,
        //              String serviceTypeName, String serviceSpeed,
        //              int providerLocationId, int serviceId)
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                                                                                //"ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID" +
                                                                                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                                                                                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                                                                                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.add(new Service(
                        resultSet.getInt("ID_SERVICE_TYPE"),
                        resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("SPEED"),
                        resultSet.getInt("ID_PROVIDER_LOCATION"),
                        resultSet.getInt("ID"),
                        resultSet.getDouble("PRICE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return result;
    }



    //KaspYar
    /**
     * Creates service location record in database from ServiceLocation object
     *  @param  serviceLocation ServiceLocation object to write
     *  @see com.naukma.cauliflower.entities.ServiceLocation
     * */
    public int createServiceLocation(ServiceLocation serviceLocation){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int res=0;
        System.out.println("CREATE SERVICE LOCATION!");

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO LOCATION(ADRESS, LONGITUDE, LATITUDE) VALUES(?,?,?)");
            preparedStatement.setString(1,serviceLocation.getLocationAddress());
            preparedStatement.setDouble(2, serviceLocation.getLocationLongitude());
            preparedStatement.setDouble(3, serviceLocation.getLocationLatitude());
            {//help
                System.out.println("Adress: "+serviceLocation.getLocationAddress() + " Longitude: " + serviceLocation.getLocationLongitude()+" Latitude: "+serviceLocation.getLocationLatitude());
            }
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID) MAX_ID FROM LOCATION");
            int id =0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) id = resultSet.getInt("MAX_ID");
            {//help
                System.out.println("MAX_ID: " + id);
            }

            preparedStatement = connection.prepareStatement("INSERT INTO SERVICELOCATION(ID_LOCATION) VALUES (?)");
            preparedStatement.setInt(1, id);
            {//help
                System.out.println("INSERT INTO SERVICELOCATION(ID_LOCATION) VALUES (?) DONE");
            }
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID) MAX_ID FROM SERVICELOCATION");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) res = resultSet.getInt("MAX_ID");
            {//help
                System.out.println("MAX_ID "+res);
            }
            connection.commit();
            {//help
                System.out.println("SUCCESSFULL COMMIT" );
            }
        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                connection.setAutoCommit(true);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return res;

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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        System.out.println("CREATE SERVICE INSTANCE!");
        int res = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO SERVICEINSTANCE(ID_USER, ID_SERVICE_LOCATION, ID_SERVICE, SERVICE_INSTANCE_STATUS) " +
                                                                                "VALUES (?,?,?,?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, serviceLocation.getServiceLocationId());
            System.out.println("SERVICE LOC ID: "+serviceLocation.getServiceLocationId());
            System.out.println("Service ID: "+ serviceId);
            preparedStatement.setInt(3, serviceId);
            preparedStatement.setInt(4, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID) MAX_ID FROM SERVICEINSTANCE");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) res = resultSet.getInt("MAX_ID");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service instance");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                connection.setAutoCommit(true);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }


        return res;
    }
    //KaspYar

    /**
     * Creates task with status FREE for installation engineer for selected service order
     * @param serviceOrderId
     * @return id of created task
     * */
    public int createTaskForInstallation(int serviceOrderId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;
        {//help
            System.out.println("CREATE TASK FOR INSTALLATION");
        }
        try {
            connection.setAutoCommit(false);
            preparedStatement=connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                    "VALUES ( " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                    "?, ?)");
            {//HELP
                System.out.println("taskStatus: " + TaskStatus.FREE.toString());
                System.out.println("userRole: "+ UserRoles.INSTALLATION_ENG.toString());
                System.out.println("serviceOrderId " + serviceOrderId);
                System.out.println("TaskName: "+ TaskName.CREATE_NEW_ROUTER.toString());
            }
            preparedStatement.setString(1, TaskStatus.FREE.toString());
            preparedStatement.setString(2, UserRoles.INSTALLATION_ENG.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, TaskName.CREATE_NEW_ROUTER.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            {//help
                System.out.println("MAX_ID: "+ taskId);
            }
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE TASK FOR INSTALLATION");
            }
        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createTaskForInstallation");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                connection.setAutoCommit(true);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return  taskId;

    }

    //KaspYar
    /**
     * Creates task with status FREE for provisioning engineer for selected service order
     * @param serviceOrderId
     * @return id of created task
     * */
    public int createTaskForProvisioning(int serviceOrderId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;

        try {
            connection.setAutoCommit(false);
            preparedStatement=connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                    "VALUES ( " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                    "?, ?);");
            preparedStatement.setString(1, TaskStatus.FREE.toString());
            preparedStatement.setString(2, UserRoles.PROVISIONING_ENG.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, TaskName.CREATE_CIRCUIT.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) taskId = resultSet.getInt("ID_TASK");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createTaskForProvisioning");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                connection.setAutoCommit(true);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return taskId;

    }

    /*
    //KaspYar
    // Нужно найти свободный порт, сделать его занятым, создать кабель на базе этого порта. Этот кабель записать в
    // ServiceInstance.
    public void createPortAndCableAndAssignToServiceInstance(int serviceInstanceId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("CALL CreateCircuit(?)");
            preparedStatement.setInt(1, serviceInstanceId);
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

    }*/


    //KaspYar
    public List<Task> getFreeAndProcessingTasksByUserRoleId(int userRoleId) {
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, T.ID_SERVICEORDER, T.NAME, TS.NAME TS_NAME " +
                                                            "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                                                            "WHERE ID_USERROLE = ? AND ((TS.NAME = ?) OR (TS.NAME = ?))");
            preparedStatement.setInt(1, userRoleId);
            preparedStatement.setString(2, TaskStatus.FREE.toString());
            preparedStatement.setString(3, TaskStatus.PROCESSING.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                //Task(int taskId, int userRoleId, int serviceOrderId,
                // int taskStatusId, String taskStatus, TaskName taskName)

                result.add(new Task(resultSet.getInt("ID_TASK"), resultSet.getInt("ID_USERROLE"), resultSet.getInt("ID_SERVICEORDER"),
                                    resultSet.getInt("ID_TASKSTATUS"), resultSet.getString("TS_NAME"), TaskName.valueOf(resultSet.getString("NAME"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        result.trimToSize();
        return result;
    }


    //KaspYar
    // Нужно найти свободный порт, сделать его занятым, создать кабель на базе этого порта. Этот кабель записать в
    // ServiceInstance, полученный из ServiceOrder

    /**
     * Creates a cable, assigns a free port to it and then assigns this cable to instance associated with service order
     * @param serviceOrderId id of service order to take service instance from
     */
    public void createPortAndCableAndAssignToServiceInstance(int serviceOrderId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int portId = 0;
        int cableId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("SELECT MIN(ID) PORT_ID FROM PORT WHERE USED = 0");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) portId = resultSet.getInt("PORT_ID");

            preparedStatement = connection.prepareStatement("INSERT INTO CABLE(ID_PORT) VALUES(?)");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT MAX(ID) CABLE_ID FROM CABLE");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) cableId = resultSet.getInt("CABLE_ID");

            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                    "SET ID_CABLE = ? " +
                    "WHERE ID = (SELECT ID_SERVICEINSTANCE FROM SERVICEORDER WHERE ID_SERVICEORDER = ?)");
            preparedStatement.setInt(1, cableId);
            preparedStatement.setInt(2,serviceOrderId);
            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                connection.setAutoCommit(true);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
    }

    //KaspYar
    /**
     *
     * @return True if a free port exists, otherwise false
     */
    public boolean freePortExists() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;

        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) AM FROM PORT WHERE USED = ?");
            preparedStatement.setInt(1, 0);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) result = (resultSet.getInt("AM") > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }

    //KaspYar
    /**
     * Returns scenario type for this service
     * @param serviceOrderId id of service
     * @return scenario of service
     */
    public Scenario getOrderScenario(int serviceOrderId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT OS.NAME " +
                    "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
                    "WHERE SO.ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, serviceOrderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) return Scenario.valueOf(resultSet.getString("NAME"));
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns status of task
     * @param taskId
     * @return status of this task
     */
    public TaskStatus getTaskStatus(int taskId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT TS.NAME " +
                    "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                    "WHERE T.ID_TASK =?");
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return TaskStatus.valueOf(resultSet.getString("NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return null;
    }



    /**
     * Returns task with given id PK
     * @param taskId id of the task
     * @return task object with selected id
     * @see com.naukma.cauliflower.entities.Task
     */
    public Task getTaskById(int taskId){
        Task task = null;
        Connection connection= getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, T.ID_SERVICEORDER, T.NAME, TS.NAME TS_NAME " +
                                                            "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                                                            "WHERE ID_TASK = ?");
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                task = new Task(resultSet.getInt("ID_TASK"), resultSet.getInt("ID_USERROLE"), resultSet.getInt("ID_SERVICEORDER"),
                        resultSet.getInt("ID_TASKSTATUS"), resultSet.getString("TS_NAME"), TaskName.valueOf(resultSet.getString("NAME")));
            }

            //Task(int taskId, int userRoleId, int serviceOrderId,
            // int taskStatusId, String taskStatus, TaskName taskName)
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                close(connection, preparedStatement);
//                if (!preparedStatement.isClosed()) preparedStatement.close();
//                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return task;

    }


    /**---------------------------------------------------------------------END KASPYAR---------------------------------------------------------------------**/


    /**---------------------------------------------------------------------IGOR---------------------------------------------------------------------**/


    /**---------------------------------------------------------------------END IGOR---------------------------------------------------------------------**/

}


