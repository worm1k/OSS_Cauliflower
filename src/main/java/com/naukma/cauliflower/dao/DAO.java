package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public enum DAO {
    INSTANCE;
    private static final Logger logger = Logger.getLogger(String.class);
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


    /**
     * ---------------------------------------------------------------------HALYA---------------------------------------------------------------------*
     */

    public int getUserRoleIdFor(UserRoles userRoles) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 0;
        preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = ?");
        preparedStatement.setString(1, userRoles.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            result = resultSet.getInt("RES");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("Smth wrong with closing connection or preparedStatement!");
            e.printStackTrace();
        }
        return result;
    }
    //Galya_Sh
    //просто отримуємо айди юзер ролі яка є Installation Engineer
//    public int getUserRoleIdFor_InstallationEngineer() {
//        Connection connection = getConnection();
//        PreparedStatement preparedStatement = null;
//        int result = 4;
//        try {
//            UserRoles urName = UserRoles.INSTALLATION_ENG;
//            preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = ?");
//            preparedStatement.setString(1, urName.toString());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                result = resultSet.getInt("RES");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                close(connection, preparedStatement);
//                //if (!preparedStatement.isClosed()) preparedStatement.close();
//                //if (!connection.isClosed()) connection.close();
//            } catch (SQLException e) {
//                logger.info("Smth wrong with closing connection or preparedStatement!");
//                e.printStackTrace();
//            }
//
//        }
//        return result;
//    }
//
//    //Galya_Sh
//    //просто отримуємо айди юзер ролі яка є Provisioning Engineer
//    public int getUserRoleIdFor_ProvisioningEngineer() {
//        Connection connection = getConnection();
//        PreparedStatement preparedStatement = null;
//        int result = 0;
//        try {
//            UserRoles urName = UserRoles.PROVISIONING_ENG;
//            preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = ?");
//            preparedStatement.setString(1, urName.toString());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                result = resultSet.getInt("RES");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                close(connection, preparedStatement);
//                //if (!preparedStatement.isClosed()) preparedStatement.close();
//                //if (!connection.isClosed()) connection.close();
//            } catch (SQLException e) {
//                logger.info("Smth wrong with closing connection or preparedStatement!");
//                e.printStackTrace();
//            }
//
//        }
//        return result;
//    }

    //Halya
    //if error - return < 0
    //else return id of created user
    public int createUser(User us, String password) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = -1;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO USERS (ID_USERROLE,E_MAIl,PASSWORD,F_NAME,L_Name,PHONE)" +
                    "VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1, us.getUserRoleId());
            preparedStatement.setString(2, us.getEmail());
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, us.getFirstName());
            preparedStatement.setString(5, us.getLastName());
            preparedStatement.setString(6, us.getPhone());
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
        } finally {
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
    public boolean checkForEmailUniq(String email) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE E_Mail = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()) {
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 0) {
                result = true;
            } else {
                result = false;
            }

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

        return result;
    }

    //Halya
    //true, if user with this id exist
    public boolean checkForExistingUserById(int id) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE Id_User = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()) {
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 1) {
                result = true;
            } else {
                result = false;
            }

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

        return result;
    }

    //Halya
    //if error, return null
    //return blocked user
    public User blockUserById(int idForBlock) {
        Connection connection = getConnection();
        User resultUser = null;
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE USERS SET Isblocked = 1 WHERE Id_User = ? ");
            preparedStatement.setInt(1, idForBlock);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("ID USER: " + idForBlock + " IS BLOCKED");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM USERS US " +
                    "INNER JOIN USERROLE UR ON US.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE ID_USER = ? ");
            preparedStatement.setInt(1, idForBlock);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUserRole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String fName = resultSet.getString("F_Name");
                String lName = resultSet.getString("L_Name");
                String phone = resultSet.getString("PHONE");
                String nameUR = resultSet.getString("NAME");
                boolean isBlocked = (resultSet.getInt("IS_BLOCKED") == 1);

                resultUser = new User(idForBlock, idUserRole, nameUR, eMail, fName, lName, phone, isBlocked);
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
        } finally {
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
    //return blocked user or null
    public User blockUserByEmail(String email) {
        Connection connection = getConnection();
        User resultUser = null;
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE USERS SET IS_BLOCKED = 1 WHERE E_MAIL = ? ");
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("USER WITH EMAIL " + email + " IS BLOCKED");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM USERS US " +
                    "INNER JOIN USERROLE UR ON US.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? ");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUserRole = resultSet.getInt("ID_USERROLE");
                int idUser = resultSet.getInt("ID_USER");
                String fName = resultSet.getString("F_Name");
                String lName = resultSet.getString("L_Name");
                String phone = resultSet.getString("PHONE");
                String nameUR = resultSet.getString("NAME");
                boolean isBlocked = (resultSet.getInt("IS_BLOCKED") == 1);

                resultUser = new User(idUser, idUserRole, nameUR, email, fName, lName, phone, isBlocked);
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
        } finally {
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
    //true if user with this email exists
    public boolean checkForExistingUserByEmail(String email) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE E_MAIL = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()) {
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 1) {
                result = true;
                {//help
                    System.out.println("USER WITH EMAIL " + email + " EXIST");
                }

            } else {
                result = false;
                {//help
                    System.out.println("THERE IS NO USER WITH EMAIL " + email);
                }
            }

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

        return result;
    }


    //Halya
    //return name of userRole or null, if no userRole with this id
    public String getUserRoleNameByUserRoleId(int userRoleId) {
        Connection connection = getConnection();
        String result = null;
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection.prepareStatement("SELECT NAME RES FROM USERROLE WHERE ID_USERROLE = ?");
            preparedStatement.setInt(1, userRoleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("RES");
            }

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
        //if(userRoleId==1)return "CUSTOMER";
        //if(userRoleId==2)return "Customer Support Engineer";
        //if(userRoleId==3)return "Provisioning Engineer";
        //if(userRoleId==4)return "Installation Engineer";
        return result;
    }

    //Halya
    //return user, if password has been change successful ,
    //else return null
    public User changeUserPasswordById(int userId, String newPassword) {
        Connection connection = getConnection();
        User resultUser = null;
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE USERS SET PASSWORD = ? WHERE Id_USER = ?");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            {//help
                System.out.println(" FOR ID USER: " + userId + " password was successfully changed");
            }

            preparedStatement = connection.prepareStatement("SELECT *  FROM USERS US " +
                    "INNER JOIN USERROLE UR ON US.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE ID_USER = ? ");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUserRole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String fName = resultSet.getString("F_Name");
                String lName = resultSet.getString("L_Name");
                String phone = resultSet.getString("PHONE");
                String nameUR = resultSet.getString("NAME");
                boolean isBlocked = (resultSet.getInt("IS_BLOCKED") == 1);

                resultUser = new User(userId, idUserRole, nameUR, eMail, fName, lName, phone, isBlocked);
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
        } finally {
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


    //Galya_Sh RI.1
    //The system should document Devices.
    // повертаємо просто всю інформацію для репорту

    public ResultSet getDevicesForReport() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT r.id ROUTER, SUM(P.Used) OCCUPIED, 60 - SUM(p.Used) FREE " +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                    "GROUP BY r.id ");
            resultSet = preparedStatement.executeQuery();
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
        return resultSet;
    }

    //Galya_Sh RI.2
    //The system should document the Ports.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getPortsForReport() throws SQLException {
        {//help
            System.out.println("getPortsForReport");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT R.Id ROUTER, P.Id PORT, P.Used USED " +
                "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                "Order By R.Id, P.Id ");
        ResultSet resultSet = preparedStatement.executeQuery();
        {//help
            System.out.println("SUCCESS!!!!getPortsForReport");
        }
        return resultSet;
    }

    //Galya_Sh RI.4
    //The system should document physical link to end user as Cable.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getCablesForReport() throws SQLException {
        {//help
            System.out.println("getCablesForReport");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT C.Id CABLE, Si.Id SERVICE_INSTANCE " +
                "FROM (Cable C INNER JOIN Serviceinstance SI ON C.Id = Si.Id_Cable) " +
                "ORDER BY C.Id");
        ResultSet resultSet = preparedStatement.executeQuery();
        {//help
            System.out.println("SUCCESS!!!!getCablesForReport");
        }
        return resultSet;
    }


    //Galya_Sh RI.5
    //The system should document logical entity of provided Service as Circuit.
    // повертаємо просто всю інформацію для репорту
    public ResultSet getCircuitsForReport() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT r.ID ROUTER, p.Id PORT, c.ID CABLE, si.ID SERVICE_INSTANCE " +
                    "FROM ((ROUTER r INNER JOIN PORT p ON r.Id = P.Id_Router) INNER JOIN CABLE c ON p.Id = C.Id_Port) " +
                    "INNER JOIN SERVICEINSTANCE si ON C.Id = Si.Id_Cable ");
            resultSet = preparedStatement.executeQuery();
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
        return resultSet;
    }


    //Halya
    //return true, if no user in db with this phone
    //else return false
    public boolean checkForPhoneUniq(String phone) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE PHONE = ?");
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            int checkResult = -1;
            if (resultSet.next()) {
                checkResult = resultSet.getInt("RES");
            }
            if (checkResult == 0) {
                result = true;
                {//help
                    System.out.println("checkForPhoneUniq PHONE " + phone + " IS UNIQ");
                }
            } else {
                result = false;
                {//help
                    System.out.println("checkForPhoneUniq USER WITH PHONE " + phone + " IS ALREADY EXISTS ");
                }
            }

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

        return result;
    }
/**---------------------------------------------------------------------END HALYA---------------------------------------------------------------------**/

/**---------------------------------------------------------------------KASPYAR---------------------------------------------------------------------**/
    //KaspYar

    /**
     * Get user by its login and password
     *
     * @param login    user login
     * @param password user password
     * @return found user or null if user does not exist
     */
    public User getUserByLoginAndPassword(String login, String password) throws SQLException {
        {//help
            System.out.println("getUserByLoginAndPassword");
        }
        Connection connection = getConnection();
        User user = null;
        PreparedStatement preparedStatement = null;
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
            boolean isBlocked = (resultSet.getInt("IS_BLOCKED") == 1);
            user = new User(idUser, idUserrole, userrole, eMail, firstName, lastName, phone, isBlocked);
        }
        resultSet.close();
        {//help
            System.out.println("SUCCESS!!! getUserByLoginAndPassword");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return user;
    }

    //KaspYar

    /**
     * Creates new service order
     *
     * @param userId            id of the user to create SO
     * @param scenario          scenario for the order
     * @param idServiceInstance id of service instance for disconnect scenario
     * @return id of created instance
     * @see com.naukma.cauliflower.dao.Scenario
     */
    public int createServiceOrder(int userId, Scenario scenario, Integer idServiceInstance) throws SQLException {

        //default status ENTERING

        OrderStatus orderStatus = OrderStatus.ENTERING;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 0;
        {//help
            System.out.println("CREATE NEW ORDER!");
        }
        connection.setAutoCommit(false);
        preparedStatement = connection.prepareStatement("SELECT ID_ORDERSCENARIO FROM ORDERSCENARIO WHERE NAME = ?");
        preparedStatement.setString(1, scenario.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        int idOrderScenario = 0;
        if (resultSet.next()) {
            idOrderScenario = resultSet.getInt("ID_ORDERSCENARIO");
        }
        {//help
            System.out.println("Scenario : " + scenario);
            System.out.println("idScenario: " + idOrderScenario);
        }

        preparedStatement = connection.prepareStatement("SELECT ID_ORDERSTATUS FROM ORDERSTATUS WHERE NAME = ?");
        preparedStatement.setString(1, orderStatus.toString());
        int idOrderStatus = 0;
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            idOrderStatus = resultSet.getInt("ID_ORDERSTATUS");
        }
        {//help
            System.out.println("idOrderStatus: " + idOrderStatus);
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        Date d = gregorianCalendar.getTime();
        if (idServiceInstance == null) {
            preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                    "VALUES(?,?,?,? )");
            preparedStatement.setInt(1, idOrderScenario);
            preparedStatement.setInt(2, idOrderStatus);
            preparedStatement.setDate(3, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
            preparedStatement.setInt(4, userId);
            {//help
                System.out.println("NULL");
            }
        } else {
            preparedStatement = connection.prepareStatement("INSERT INTO SERVICEORDER(ID_SRVICEINSTANCE, ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                    "VALUES(?, ?,? ,?,?)");
            preparedStatement.setInt(1, idServiceInstance.intValue());
            preparedStatement.setInt(2, idOrderScenario);
            preparedStatement.setInt(3, idOrderStatus);
            preparedStatement.setDate(4, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
            preparedStatement.setInt(5, userId);
            {//help
                System.out.println("NOT NULL");
                System.out.println("idServiceInstance " + idServiceInstance.intValue());
            }
        }
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement("SELECT MAX(ID_SERVICEORDER) RES FROM SERVICEORDER");
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            {//help
                System.out.println("RETURN : " + resultSet.getInt("RES"));
            }
            result = resultSet.getInt("RES");
        }

        {//help
            System.out.println("SUCCESS! CREATE NEW ORDER!");
        }
        try {
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
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("Smth wrong with closing connection or preparedStatement!");
            e.printStackTrace();
        }
        return result;
    }

    //KaspYar

    /**
     * Connects selected instance and selected user
     *
     * @param instanceId id of the instance
     * @param userId     id of the user    *
     */
    public void setUserForInstance(int instanceId, int userId) throws SQLException {
        {//help
            System.out.println("SET USER FOR INSTANCE!");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                "SET ID_USER = ? " +
                "WHERE ID = ?");
        preparedStatement.setInt(1, userId);
        preparedStatement.setInt(2, instanceId);
        {//help
            System.out.println("instanceId: " + instanceId);
            System.out.println("userId: " + userId);
        }
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS!!! SET USER FOR INSTANCE!");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
    }

    //KaspYar

    /**
     * Set selected status for selected instance
     *
     * @param instanceId id of the instance
     * @param status     status of the instance
     */
    public void changeInstanceStatus(int instanceId, InstanceStatus status) throws SQLException {
        {//help
            System.out.println("CHANGE INSTANCE STATUS!");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                "SET SERVICE_INSTANCE_STATUS = (SELECT ID " +
                "FROM SERVICEINSTANCESTATUS " +
                "WHERE NAME = ?) " +
                "WHERE ID = ?");
        preparedStatement.setString(1, status.toString());
        preparedStatement.setInt(2, instanceId);
        {//help
            System.out.println("InstanceId: " + instanceId);
            System.out.println("Status : " + status.toString());
        }
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS!!! CHANGE INSTANCE STATUS!");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
    }

    //KaspYar

    /**
     * Set selected status for selected order
     *
     * @param orderId     id of the order
     * @param orderStatus status of the task
     */
    public void changeOrderStatus(int orderId, OrderStatus orderStatus) throws SQLException {
        {//help
            System.out.println("CHANGE ORDER STATUS");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("UPDATE SERVICEORDER " +
                "SET ID_ORDERSTATUS = (SELECT ID_ORDERSTATUS " +
                "FROM ORDERSTATUS " +
                "WHERE NAME = ?) " +
                "WHERE ID_SERVICEORDER = ?");

        preparedStatement.setString(1, orderStatus.toString());
        preparedStatement.setInt(2, orderId);
        {//help
            System.out.println("orderId: " + orderId);
            System.out.println("orderStatus: " + orderStatus.toString());
        }
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS!!! CHANGE ORDER STATUS");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
    }

    //KaspYar

    /**
     * Set selected status for selected task
     *
     * @param taskId     id of the task
     * @param taskStatus status of the task
     */
    public void changeTaskStatus(int taskId, TaskStatus taskStatus) throws SQLException {
        {//help
            System.out.println("CHANGE TASK STATUS");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("UPDATE TASK " +
                "SET ID_TASKSTATUS = (SELECT ID_TASKSTATUS " +
                "FROM TASKSTATUS " +
                "WHERE NAME = ?) " +
                "WHERE ID_TASK = ?");

        preparedStatement.setString(1, taskStatus.toString());
        preparedStatement.setInt(2, taskId);
        {//help
            System.out.println("taskId: " + taskId);
            System.out.println("taskStatus: " + taskStatus.toString());
        }
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS!!! CHANGE TASK STATUS");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
    }

    //KaspYar

    /**
     * Block instance when exist active task on it
     *
     * @param instanceId id of the instance
     * @param isBlocked  0 - set instance not blocked, 1 - set instance blocked
     */
    public void setInstanceBlocked(int instanceId, int isBlocked) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        {//help
            System.out.println("SET INSTANCE BLOCKED!");
        }
        preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                "SET HAS_ACTIVE_TASK = ? " +
                "WHERE ID = ?");
        preparedStatement.setInt(1, isBlocked);
        preparedStatement.setInt(2, instanceId);
        {//help
            System.out.println("instanceId: " + instanceId);
            System.out.println("blocked: " + isBlocked);
        }
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS! SET INSTANCE BLOCKED!");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
    }

    //KaspYar

    /**
     * returns List<Task>
     *
     * @param taskStatusId id of TaskStatus
     * @param userRoleId   id of UserRole
     * @return
     */
    public List<Task> getTasksByStatusAndRole(int taskStatusId, int userRoleId) throws SQLException {
        {//help
            System.out.println("getTasksByStatusAndRole");
        }
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_USERROLE, T.ID_SERVICEORDER, T.ID_TASKSTATUS, TS.NAME TS_NAME, T.NAME T_NAME " +
                "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                "WHERE T.ID_TASKSTATUS = ? AND T.ID_USERROLE = ?");
        preparedStatement.setInt(1, taskStatusId);
        preparedStatement.setInt(2, userRoleId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(new Task(resultSet.getInt("ID_TASK"),
                    resultSet.getInt("ID_USERROLE"),
                    resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_TASKSTATUS"),
                    resultSet.getString("TS_NAME"),
                    TaskName.valueOf(resultSet.getString("T_NAME"))));

        }
        {//help
            System.out.println("success!   getTasksByStatusAndRole");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }
    //KaspYar

    /**
     * returns all Services of Provider Location with certain id
     *
     * @param providerLocationId id of Provider Location
     * @return ArrayList<Service>
     */

    public List<Service> getServicesByProviderLocationId(int providerLocationId) throws SQLException {
        {//help
            System.out.println("getServicesByProviderLocationId");
        }
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID " +
                "WHERE S.ID_PROVIDER_LOCATION = ? ");
        preparedStatement.setInt(1, providerLocationId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(new Service(resultSet.getInt("ID_SERVICE_TYPE"),
                    resultSet.getString("ADRESS"),
                    resultSet.getDouble("LONGITUDE"),
                    resultSet.getDouble("LATITUDE"),
                    resultSet.getString("NAME"),
                    resultSet.getString("SPEED"),
                    resultSet.getInt("ID_PROVIDER_LOCATION"),
                    resultSet.getInt("ID"),
                    resultSet.getDouble("PRICE")));
        }
        {//help
            System.out.println("SUCCESS !!! getServicesByProviderLocationId");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Creates new router
     */
    public void createRouter() {
        {//help
            System.out.println("CREATE ROUTER");
        }
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
            if (resultSet.next()) {
                idRouter = resultSet.getInt("M");
            }
            StringBuilder sb = new StringBuilder("INSERT ALL ");
            for (int i = 1; i <= amountsOfPorts; i++) sb.append("INTO PORT(ID_ROUTER) VALUES(" + idRouter + ") ");
            sb.append("SELECT * FROM DUAL");
            preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.executeUpdate();
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE NEW ROUTER");
            }
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
        } finally {
            try {
                close(connection, preparedStatement);

            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
    }

    //KaspYar

    /**
     * Returns ServiceOrder for selected task
     *
     * @param taskId Id of the task
     */

    public ServiceOrder getServiceOrder(int taskId) throws SQLException {
        {//help
            System.out.println("getServiceOrder");
        }
        ServiceOrder result = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OST.NAME OST_NAME, " +
                "SO.ID_SRVICEINSTANCE, SO.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                "FROM (((TASK T INNER JOIN SERVICEORDER SO ON T.ID_SERVICEORDER = SO.ID_SERVICEORDER) " +
                "INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS " +
                ") INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
                "WHERE T.ID_TASK = ?");
        preparedStatement.setInt(1, taskId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date date = resultSet.getDate("OUR_DATE");
            gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
            result = new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_ORDERSTATUS"),
                    resultSet.getString("OST_NAME"),
                    resultSet.getInt("ID_SRVICEINSTANCE"),
                    resultSet.getInt("ID_ORDERSCENARIO"),
                    resultSet.getString("OSC_NAME"),
                    gregorianCalendar, resultSet.getInt("ID_USER"));
        }
        {//help
            System.out.println("SUCCESS ! getServiceOrder");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return result;
    }


    //KaspYar

    /**
     * Connects selected instance and selected order
     *
     * @param instanceId id of the instance
     * @param orderId    id of the order    *
     */
    public void setInstanceForOrder(int instanceId, int orderId) throws SQLException {
        {//help
            System.out.println("SET INSTANCE FOR ORDER");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("UPDATE SERVICEORDER " +
                "SET ID_SRVICEINSTANCE = ? " +
                "WHERE ID_SERVICEORDER = ?");
        preparedStatement.setInt(1, instanceId);
        preparedStatement.setInt(2, orderId);
        preparedStatement.executeUpdate();
        {//help
            System.out.println("SUCCESS!!! SET INSTANCE FOR ORDER");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return;
    }


    //KaspYar

    /**
     * Returns ArrayList of orders for selected user
     *
     * @param userId Id of the user
     */

    public ArrayList<ServiceOrder> getOrders(int userId) throws SQLException {
        {//help
            System.out.println("GET ORDERS");
        }
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OST_NAME, " +
                "SO.ID_SRVICEINSTANCE, OSC.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                "FROM (SERVICEORDER SO INNER JOIN  ORDERSTATUS OS ON SO.ID_ORDERSTATUS = OS.ID_ORDERSTATUS) " +
                "INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO " +
                "WHERE SO.ID_USER = ?");
        preparedStatement.setInt(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date date = resultSet.getDate("OUR_DATE");
            gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
            result.add(new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_ORDERSTATUS"),
                    resultSet.getString("OST_NAME"),
                    resultSet.getInt("ID_SRVICEINSTANCE"),
                    resultSet.getInt("ID_ORDERSCENARIO"),
                    resultSet.getString("OSC_NAME"),
                    gregorianCalendar,
                    resultSet.getInt("ID_USER")));
        }
        {//help
            System.out.println("SUCCESS ! GET ORDERS");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Returns ArrayList of instances for selected user
     *
     * @param userId Id of the user
     */

    public ArrayList<ServiceInstance> getInstances(int userId) throws SQLException {
        {//help
            System.out.println("GET INSTANCES");
        }
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
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
        while (resultSet.next()) {
            result.add(new ServiceInstance(resultSet.getInt("ID"),
                    resultSet.getInt("ID_USER"),
                    resultSet.getInt("ID_SERVICE_LOCATION"),
                    resultSet.getString("ADRESS"),
                    resultSet.getDouble("LONGITUDE"),
                    resultSet.getDouble("LATITUDE"),
                    resultSet.getInt("ID_SERVICE"),
                    resultSet.getInt("SERVICE_INSTANCE_STATUS"),
                    resultSet.getString("NAME"),
                    resultSet.getInt("ID_CABLE"),
                    (resultSet.getInt("HAS_ACTIVE_TASK") == 1)));
        }
        {//help
            System.out.println("SUCCESS !!! GET INSTANCES");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;

    }

    //KaspYar

    /**
     * Returns ArrayList of all orders
     */
    public ArrayList<ServiceOrder> getAllOrders() throws SQLException {
        {//help
            System.out.println("GET ALL ORDERS");
        }
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OS_NAME, " +
                "SO.ID_SRVICEINSTANCE, OSC.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                "FROM (SERVICEORDER SO INNER JOIN  ORDERSTATUS OS ON SO.ID_ORDERSTATUS = OS.ID_ORDERSTATUS) " +
                "INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date date = resultSet.getDate("OUR_DATE");
            gregorianCalendar.set(date.getYear(), date.getMonth(), date.getDay());
            result.add(new ServiceOrder(resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_ORDERSTATUS"),
                    resultSet.getString("OS_NAME"),
                    resultSet.getInt("ID_SRVICEINSTANCE"),
                    resultSet.getInt("ID_ORDERSCENARIO"),
                    resultSet.getString("OSC_NAME"),
                    gregorianCalendar,
                    resultSet.getInt("ID_USER")));
        }
        {//help
            System.out.println("SUCCESS GET ALL ORDERS");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;

    }
    //KaspYar

    /**
     * Returns ArrayList of all instances
     */
    public ArrayList<ServiceInstance> getAllInstances() throws SQLException {
        {//help
            System.out.println("GET ALL INSTANCES");
        }
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.HAS_ACTIVE_TASK, " +
                "L.ADRESS,L.LATITUDE, L.LONGITUDE, SIS.NAME " +
                "FROM (SERVICEINSTANCE SI INNER JOIN (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                "ON SI.ID_SERVICE_LOCATION = SL.ID)" +
                "INNER JOIN SERVICEINSTANCESTATUS SIS " +
                "ON SI.SERVICE_INSTANCE_STATUS = SIS.ID ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(new ServiceInstance(resultSet.getInt("ID"),
                    resultSet.getInt("ID_USER"),
                    resultSet.getInt("ID_SERVICE_LOCATION"),
                    resultSet.getString("ADRESS"),
                    resultSet.getDouble("LONGITUDE"),
                    resultSet.getDouble("LATITUDE"),
                    resultSet.getInt("ID_SERVICE"),
                    resultSet.getInt("SERVICE_INSTANCE_STATUS"),
                    resultSet.getString("NAME"),
                    resultSet.getInt("ID_CABLE"),
                    (resultSet.getInt("HAS_ACTIVE_TASK") == 1)));
        }
        {//help
            System.out.println("SUCCESS !! GET ALL INSTANCES");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;

    }

    //mystic function for XLS - generator
    public ResultSet reportTester() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT * FROM USERS");
        ResultSet resultSet = preparedStatement.executeQuery();
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return resultSet;
    }

    //KaspYar

    /**
     * @return all Provider Locations
     */
    public List<ProviderLocation> getProviderLocations() throws SQLException {
        {//
            System.out.println("getProviderLocations");
        }
        ArrayList<ProviderLocation> result = new ArrayList<ProviderLocation>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT * " +
                "FROM PROVIDERLOCATION PL INNER JOIN LOCATION L ON PL.ID_LOCATION = L.ID");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(new ProviderLocation(resultSet.getInt("ID"),
                    resultSet.getString("ADRESS"),
                    resultSet.getDouble("LONGITUDE"),
                    resultSet.getDouble("LATITUDE")));
        }
        {//
            System.out.println("SUCCESS!!! getProviderLocations");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }

    //vladmyr

    /**
     * @return Service by Id
     */
    public Service getServiceById(int serviceId) throws SQLException {
        {//help
            System.out.println("getServiceById");
        }
        Service service = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID WHERE S.ID = ?");
        preparedStatement.setInt(1, serviceId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            service = new Service(
                    resultSet.getInt("ID_SERVICE_TYPE"),
                    resultSet.getString("ADRESS"),
                    resultSet.getDouble("LONGITUDE"),
                    resultSet.getDouble("LATITUDE"),
                    resultSet.getString("NAME"),
                    resultSet.getString("SPEED"),
                    resultSet.getInt("ID_PROVIDER_LOCATION"),
                    resultSet.getInt("ID"),
                    resultSet.getDouble("PRICE")
            );
        }
        {//help
            System.out.println("getServiceById");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return service;

    }

    //KaspYar

    /**
     * return List<Services> of all Services
     *
     * @return
     */
    public List<Service> getServices() throws SQLException {
        {//
            System.out.println("getServices");
        }
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
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
        {//
            System.out.println("SUCCESS!!! getServices");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Creates service location record in database from ServiceLocation object
     *
     * @param serviceLocation ServiceLocation object to write
     * @see com.naukma.cauliflower.entities.ServiceLocation
     */

    public int createServiceLocation(ServiceLocation serviceLocation) {
        {//help
            System.out.println("CREATE SERVICE LOCATION!");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int res = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO LOCATION(ADRESS, LONGITUDE, LATITUDE) VALUES(?,?,?)");
            preparedStatement.setString(1, serviceLocation.getLocationAddress());
            preparedStatement.setDouble(2, serviceLocation.getLocationLongitude());
            preparedStatement.setDouble(3, serviceLocation.getLocationLatitude());
            {//help
                System.out.println("Adress: " + serviceLocation.getLocationAddress() +
                        " Longitude: " + serviceLocation.getLocationLongitude() +
                        " Latitude: " + serviceLocation.getLocationLatitude());
            }
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID) MAX_ID FROM LOCATION");
            int id = 0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) id = resultSet.getInt("MAX_ID");
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
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            {//help
                System.out.println("MAX_ID " + res);
            }
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE SERVICE LOCATION!");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
     *
     * @param userId          selected user id
     * @param serviceLocation location for the instance
     * @param serviceId       in of selected service
     * @return id of created instance
     */

    public int createServiceInstance(int userId, ServiceLocation serviceLocation, int serviceId) {
        {//help
            System.out.println("CREATE SERVICE INSTANCE!");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int res = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO SERVICEINSTANCE(ID_USER, ID_SERVICE_LOCATION, " +
                    "ID_SERVICE, SERVICE_INSTANCE_STATUS) " +
                    "VALUES (?,?,?,?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, serviceLocation.getServiceLocationId());
            System.out.println("SERVICE LOC ID: " + serviceLocation.getServiceLocationId());
            System.out.println("Service ID: " + serviceId);
            preparedStatement.setInt(3, serviceId);
            preparedStatement.setInt(4, 1);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID) MAX_ID FROM SERVICEINSTANCE");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            connection.commit();
            {//help
                System.out.println("SUCCESS !! CREATE SERVICE INSTANCE!");
            }
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
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return res;
    }


    //KaspYar

    /**
     * Creates task with status FREE for selected engineer for selected service order
     *
     * @param serviceOrderId
     * @param role           role of the engineer
     * @param taskName       name of the task
     * @return id of created task
     */
    public int createNewTask(int serviceOrderId, UserRoles role, TaskName taskName) {
        {//help
            System.out.println("CREATE TASK");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                    "VALUES ( " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                    "?, ?)");
            {//HELP
                System.out.println("taskStatus: " + TaskStatus.FREE.toString());
                System.out.println("userRole: " + role.toString());
                System.out.println("serviceOrderId " + serviceOrderId);
                System.out.println("TaskName: " + taskName.toString());
            }
            preparedStatement.setString(1, TaskStatus.FREE.toString());
            preparedStatement.setString(2, role.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, taskName.toString());
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            {//help
                System.out.println("MAX_ID: " + taskId);
            }
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE TASK");
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
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return taskId;
    }


    /**
     * Creates task with status FREE for installation engineer for selected service order
     *
     * @param serviceOrderId
     * @return id of created task
     */
    public int createTaskForInstallation(int serviceOrderId) {
        {//help
            System.out.println("CREATE TASK FOR INSTALLATION");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                    "VALUES ( " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                    "?, ?)");
            {//HELP
                System.out.println("taskStatus: " + TaskStatus.FREE.toString());
                System.out.println("userRole: " + UserRoles.INSTALLATION_ENG.toString());
                System.out.println("serviceOrderId " + serviceOrderId);
                System.out.println("TaskName: " + TaskName.CONNECT_NEW_PERSON.toString());
            }
            preparedStatement.setString(1, TaskStatus.FREE.toString());
            preparedStatement.setString(2, UserRoles.INSTALLATION_ENG.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, TaskName.CONNECT_NEW_PERSON.toString());
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            {//help
                System.out.println("MAX_ID: " + taskId);
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
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return taskId;
    }

    //KaspYar

    /**
     * Creates task with status FREE for provisioning engineer for selected service order
     *
     * @param serviceOrderId
     * @return id of created task
     */
    public int createTaskForProvisioning(int serviceOrderId) {
        {//help
            System.out.println("CREATE TASK FOR PROVISIONING");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                    "VALUES ( " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                    "?, ?)");
            {//help
                System.out.println("TaskStatus: " + TaskStatus.FREE.toString());
                System.out.println("userRole: " + UserRoles.PROVISIONING_ENG.toString());
                System.out.println("idServiceOrder " + serviceOrderId);
                System.out.println("taskName: " + TaskName.CREATE_CIRCUIT.toString());
            }
            preparedStatement.setString(1, TaskStatus.FREE.toString());
            preparedStatement.setString(2, UserRoles.PROVISIONING_ENG.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, TaskName.CREATE_CIRCUIT.toString());
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE TASK FOR PROVISIONING");
            }
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
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return taskId;
    }

    //KaspYar
    public List<Task> getFreeAndProcessingTasksByUserRoleId(int userRoleId) throws SQLException {
        {//help
            System.out.println("getFreeAndProcessingTasksByUserRoleId");
        }
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, T.ID_SERVICEORDER, " +
                "T.NAME, TS.NAME TS_NAME " +
                "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                "WHERE ID_USERROLE = ? AND ((TS.NAME = ?) OR (TS.NAME = ?))");
        preparedStatement.setInt(1, userRoleId);
        preparedStatement.setString(2, TaskStatus.FREE.toString());
        preparedStatement.setString(3, TaskStatus.PROCESSING.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(new Task(resultSet.getInt("ID_TASK"),
                    resultSet.getInt("ID_USERROLE"),
                    resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_TASKSTATUS"),
                    resultSet.getString("TS_NAME"),
                    TaskName.valueOf(resultSet.getString("NAME"))));
        }
        {//help
            System.out.println("SUCCESS!!! getFreeAndProcessingTasksByUserRoleId");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        result.trimToSize();
        return result;
    }


    //KaspYar
    // Нужно найти свободный порт, сделать его занятым, создать кабель на базе этого порта. Этот кабель записать в
    // ServiceInstance, полученный из ServiceOrder

    /**
     * Creates a cable, assigns a free port to it and then assigns this cable to instance associated with service order
     *
     * @param serviceOrderId id of service order to take service instance from
     */
    public void createPortAndCableAndAssignToServiceInstance(int serviceOrderId) {
        {//help
            System.out.println("createPortAndCableAndAssignToServiceInstance");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int portId = 0;
        int cableId = 0;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("SELECT MIN(ID) PORT_ID FROM PORT WHERE USED = 0");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) portId = resultSet.getInt("PORT_ID");
            preparedStatement = connection.prepareStatement("INSERT INTO CABLE(ID_PORT) VALUES(?)");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT MAX(ID) CABLE_ID FROM CABLE");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) cableId = resultSet.getInt("CABLE_ID");
            preparedStatement = connection.prepareStatement("UPDATE SERVICEINSTANCE " +
                    "SET ID_CABLE = ? " +
                    "WHERE ID = (SELECT ID_SRVICEINSTANCE FROM SERVICEORDER WHERE ID_SERVICEORDER = ?)");
            preparedStatement.setInt(1, cableId);
            preparedStatement.setInt(2, serviceOrderId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("UPDATE PORT SET USED = 1 WHERE ID = ?");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            connection.commit();
            {//help
                System.out.println("SUCCESS!!! createPortAndCableAndAssignToServiceInstance");
            }
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
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
    }

    //KaspYar

    /**
     * @return True if a free port exists, otherwise false
     */
    public boolean freePortExists() throws SQLException {
        {//help
            System.out.println("freePortExists");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        preparedStatement = connection.prepareStatement("SELECT COUNT(*) AM FROM PORT WHERE USED = ?");
        preparedStatement.setInt(1, 0);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) result = (resultSet.getInt("AM") > 0);
        {//help
            System.out.println("SUCCESS!!! freePortExists");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return result;
    }

    //KaspYar

    /**
     * Returns scenario type for this service
     *
     * @param serviceOrderId id of service
     * @return scenario of service
     */
    public Scenario getOrderScenario(int serviceOrderId) throws SQLException {
        {//help
            System.out.println("getOrderScenario");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        Scenario result = null;
        preparedStatement = connection.prepareStatement("SELECT OS.NAME " +
                "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
                "WHERE SO.ID_SERVICEORDER = ?");
        preparedStatement.setInt(1, serviceOrderId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) result = Scenario.valueOf(resultSet.getString("NAME"));
        {//help
            System.out.println("SUCCESS!!! getOrderScenario");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return result;
    }

    /**
     * Returns status of task
     *
     * @param taskId
     * @return status of this task
     */
    public TaskStatus getTaskStatus(int taskId) throws SQLException {
        {//help
            System.out.println("getTaskStatus");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        TaskStatus result = null;
        preparedStatement = connection.prepareStatement("SELECT TS.NAME " +
                "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                "WHERE T.ID_TASK =?");
        preparedStatement.setInt(1, taskId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            result = TaskStatus.valueOf(resultSet.getString("NAME"));
        }
        {//help
            System.out.println("SUCCESS!!!getTaskStatus");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return result;
    }


    /**
     * Returns task with given id PK
     *
     * @param taskId id of the task
     * @return task object with selected id
     * @see com.naukma.cauliflower.entities.Task
     */
    public Task getTaskById(int taskId) throws SQLException {
        {//help
            System.out.println("getTaskById");
        }
        Task task = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, " +
                "T.ID_SERVICEORDER, T.NAME, TS.NAME TS_NAME " +
                "FROM TASK T INNER JOIN TASKSTATUS TS ON T.ID_TASKSTATUS = TS.ID_TASKSTATUS " +
                "WHERE ID_TASK = ?");
        preparedStatement.setInt(1, taskId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            task = new Task(resultSet.getInt("ID_TASK"),
                    resultSet.getInt("ID_USERROLE"),
                    resultSet.getInt("ID_SERVICEORDER"),
                    resultSet.getInt("ID_TASKSTATUS"),
                    resultSet.getString("TS_NAME"),
                    TaskName.valueOf(resultSet.getString("NAME")));
        }
        {//help
            System.out.println("SUCCESS!!!getTaskById");
        }
        try {
            close(connection, preparedStatement);
        } catch (SQLException exc) {
            logger.warn("Can't close connection or preparedStatement!");
            exc.printStackTrace();
        }
        return task;
    }

    public ResultSet getMostProfitableRouterForReport() throws SQLException {
        {//help
            System.out.println("getMostProfitableRouterForReport");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT P.ID_ROUTER, SUM(S.PRICE) PROFIT" +
                "FROM SERVICE S INNER JOIN ( " +
                "  SERVICEINSTANCE SI INNER JOIN ( " +
                "    CABLE C INNER JOIN PORT P ON C.ID_PORT = P.ID)  " +
                "  ON SI.ID_CABLE = C.ID) " +
                "ON  S.ID = SI.ID_SERVICE " +
                "GROUP BY P.ID_ROUTER ORDER BY PROFIT DESC");
        ResultSet resultSet = preparedStatement.executeQuery();
        {//help
            System.out.println("SUCCESS!!!!getMostProfitableRouterForReport");
        }
        return resultSet;
    }

    /**---------------------------------------------------------------------END KASPYAR---------------------------------------------------------------------**/


    /**
     * ---------------------------------------------------------------------IGOR---------------------------------------------------------------------*
     */


    public ResultSet getUsedRoutersAndCapacityOfPorts() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        preparedStatement = connection.prepareStatement("SELECT r.id ROUTER, 60 - SUM(P.Used) FREE,  SUM(p.Used) OCCUPIED, " +
                "ROUND((SUM(p.Used))/( 60 - SUM(P.Used)), 2) UTILIZATION\n" +
                "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) \n" +
                "GROUP BY r.id ");
        resultSet = preparedStatement.executeQuery();

        try {
            close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("Smth wrong with closing connection or preparedStatement!");
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getProfitabilityByMonth() throws SQLException {
        {//help
            System.out.println("getProfitabilityByMonth");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT P.ID_ROUTER, SUM(S.PRICE) PROFIT" +
                "FROM SERVICE S INNER JOIN ( " +
                "  SERVICEINSTANCE SI INNER JOIN ( " +
                "    CABLE C INNER JOIN PORT P ON C.ID_PORT = P.ID)  " +
                "  ON SI.ID_CABLE = C.ID) " +
                "ON  S.ID = SI.ID_SERVICE " +
                "GROUP BY P.ID_ROUTER ");
        ResultSet resultSet = preparedStatement.executeQuery();
        try {
            close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.warn("Smth wrong with closing connection or preparedStatement!");
        }
        {//help
            System.out.println("SUCCESS!!!!getProfitabilityByMonth");
        }
        return resultSet;
    }

    public ResultSet getOrdersPerPeriod(Scenario scenario, java.sql.Date sqlStartDate, java.sql.Date sqlEndDate) throws SQLException {
        {//help
            System.out.println("getOrdersPerPeriod");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT OS.NAME SCENARIO, COUNT(*) AMOUNT " +
                "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
                "WHERE OS.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? " +
                "GROUP BY OS.NAME ");
        preparedStatement.setString(1, scenario.toString());
        preparedStatement.setDate(2, sqlStartDate);
        preparedStatement.setDate(3, sqlEndDate);
        ResultSet resultSet = preparedStatement.executeQuery();
        try {
            close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.warn("Smth wrong with closing connection or preparedStatement!");
        }
        {//help
            System.out.println("SUCCESS!!!!getOrdersPerPeriod");
        }
        return resultSet;
    }
//
//    public ResultSet getNewOrdersPerPeriod(java.sql.Date sqlStartDate, java.sql.Date sqlEndDate) throws SQLException{
//        {//help
//            System.out.println("getNewOrdersPerPeriod");
//        }
//        Connection connection = getConnection();
//        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) " +
//                "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
//                "WHERE OS.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? ");
//        preparedStatement.setString(1, Scenario.NEW.toString());
//        preparedStatement.setDate(2, sqlStartDate);
//        preparedStatement.setDate(3, sqlEndDate);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        try {
//            close(connection, preparedStatement);
//        }catch (SQLException e){
//            logger.warn("Smth wrong with closing connection or preparedStatement!");
//        }
//        {//help
//            System.out.println("SUCCESS!!!!getNewOrdersPerPeriod");
//        }
//        return resultSet;
//    }
//
//    public ResultSet DisconnectOrdersPerPeriod(java.sql.Date sqlStartDate, java.sql.Date sqlEndDate) {
//        return null;
//    }

    //IGOR RI.6
    //Получить ServiceInstance по OrderId. По cable_id получить привязанный порт и сделать его свободным. cable_id в ServiceInstance
    //сделать равным null. Сам кабель удалить из базы.
    //The system should allow deleting of Cables and Circuits.
    public void removeCableFromServiceInstanceAndFreePort(int serviceOrderId) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement preparedStatementSelect = null;
        PreparedStatement preparedStatementUpdate = null;
        ResultSet resultSet = null;
        final int checkNumber = -1;
        int siID = checkNumber;
        int cableID = checkNumber;
        int portID = checkNumber;

        try {
            connection.setAutoCommit(false);
            // ---- GET SI ID, CABLE ID, PORT ID BY SO ID
            preparedStatementSelect = connection
                    .prepareStatement("SELECT SI.ID, C.ID_CABLE, C.ID_PORT "
                            + "FROM (SERVICEORDER SO INNER JOIN SERVICEINSTANCE SI ON SI.ID = SO.ID_SRVICEINSTANCE) "
                            + "INNER JOIN CABLE C ON SI.ID_CABLE=C.ID "
                            + "WHERE SO.ID = ?");
            preparedStatementSelect.setInt(1, serviceOrderId);

            resultSet = preparedStatementSelect.executeQuery();
            while (resultSet.next()) {
                siID = resultSet.getInt(1);
                cableID = resultSet.getInt(2);
                portID = resultSet.getInt(3);
            }

            if (siID != checkNumber && cableID != checkNumber
                    && portID != checkNumber) {

                // ---- UPDATE PORT USED SET 0
                preparedStatementUpdate = connection
                        .prepareStatement("UPDATE PORT USED SET 0 WHERE ID = ?");
                preparedStatementSelect.setInt(1, portID);
                preparedStatementUpdate.execute();

                // ---- UPDATE SERVICEINSTANCE ID_CABLE SET NULL
                preparedStatementUpdate = connection
                        .prepareStatement("UPDATE SERVICEINSTANCE ID_CABLE SET NULL WHERE ID = ?");
                preparedStatementSelect.setInt(1, siID);
                preparedStatementUpdate.execute();

                // ---- DELETE FROM CABLE WHERE ID = cableID
                preparedStatementUpdate = connection
                        .prepareStatement("DELETE FROM CABLE WHERE ID = ?");
                preparedStatementSelect.setInt(1, cableID);
                preparedStatementUpdate.execute();
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatementSelect);
                close(connection, preparedStatementUpdate);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
    }


    /**---------------------------------------------------------------------END IGOR---------------------------------------------------------------------**/

}


