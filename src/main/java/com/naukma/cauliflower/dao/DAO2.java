package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import com.naukma.cauliflower.reports.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public enum DAO2 {
    INSTANCE;
    private static final Logger logger = Logger.getLogger(String.class);
    private DataSource dataSource;
    private static final String BD_JNDI = "jdbc/oraclesource"; // no magic numbers

    private DAO2() {
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
     * ---------------------------------------------------------------------Eugene Try---------------------------------------------------------------------*
     */

    private HashMap<String, PreparedStatementBlocker> hashMapForPreparedStatement;
    private Connection connectionForPreparedStatements;

    /**
     * get PreparedStatement from hashMapForPreparedStatement and block it or add it there and block
     *
     * @param key    key for returned PreparedStatementBlocker object in hashMapForPreparedStatement, if there's such
     * @param query query for returned PreparedStatementBlocker object in hashMapForPreparedStatement
     * @return PreparedStatementBlocker for query
     */
    private PreparedStatementBlocker getPreparedStatementFromHashMap(String key, String query) throws SQLException {
        if(hashMapForPreparedStatement.containsKey(key)) {
            PreparedStatementBlocker ret = hashMapForPreparedStatement.get(key);
            if(ret.blocked()){

                {//help
                    System.out.println("Created new PreparedStatementBlocker");
                }
                return new PreparedStatementBlocker(query, getConnection(), true);
            }else {

                {//help
                    System.out.println("Get from hashMapForPreparedStatement PreparedStatementBlocker");
                }
                return ret.block();
            }
        }else{
            {//help
                System.out.println("put new to hashMapForPreparedStatement PreparedStatementBlocker");
            }
            PreparedStatementBlocker ret =  new PreparedStatementBlocker(query, connectionForPreparedStatements);
            hashMapForPreparedStatement.put(key, ret);
            return ret;
        }
    }

    /**---------------------------------------------------------------------END EUGENE TRY---------------------------------------------------------------------**/


    /**
     * ---------------------------------------------------------------------HALYA---------------------------------------------------------------------*
     */

    /**
     * Returns id of specific UserRole
     *
     * @param userRole UserRole to get id for
     * @return id of specific UserRole in database
     * @throws SQLException
     */
    public int getUserRoleIdFor(UserRole userRole) throws SQLException {
        Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int result = 0;
        preparedStatement =
                getPreparedStatementFromHashMap("getUserRoleIdFor request", "SELECT Id_UserRole RES FROM USERROLE WHERE NAME = ?");
        preparedStatement.setString(1, userRole.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            result = resultSet.getInt("RES");
        }
        try {
            //close(connection, preparedStatement);
            preparedStatement.close();
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

    /**
     * @param us       User to create
     * @param password User password
     * @return -1 if error occured, otherwise id of created user
     */
    public int createUser(User us, String password) throws SQLException  {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int result = -1;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement =
                    getPreparedStatementFromHashMap("createUser1", "INSERT INTO USERS (ID_USERROLE,E_MAIl,PASSWORD,F_NAME,L_Name,PHONE)" +
                            "VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1, us.getUserRoleId());
            preparedStatement.setString(2, us.getEmail());
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, us.getFirstName());
            preparedStatement.setString(5, us.getLastName());
            preparedStatement.setString(6, us.getPhone());
            preparedStatement.executeUpdate();

            preparedStatement = getPreparedStatementFromHashMap("createUser2", "SELECT MAX(ID_USER) MAX_ID FROM USERS");
            //int idU = 0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("MAX_ID");
            }
            preparedStatement.commit();
        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        } finally {
            try {
                //connection.setAutoCommit(true);
                //close(connection, preparedStatement);
                preparedStatement.close();
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return result;
    }


    /**
     * Checks if user with specified email exists in database.
     *
     * @param email email to check
     * @return true if exists, false if doesn't
     */
    public boolean checkForEmailUniq(String email) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("checkForEmailUniq", "SELECT COUNT(Id_User) RES FROM USERS WHERE E_Mail = ?");
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

        } finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }


    /**
     * Checks if user with specified id exists in database.
     *
     * @param id id to check
     * @return true if user exists, false if doesn't
     */
    public boolean checkForExistingUserById(int id) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("checkForExistingUserById", "SELECT COUNT(Id_User) RES FROM USERS WHERE Id_User = ?");//connection.prepareStatement("SELECT COUNT(Id_User) RES FROM USERS WHERE Id_User = ?");
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

        }  finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }


    /**
     * Blocks a user by his id.
     *
     * @param idForBlock id of a user who should be blocked
     * @return Null if error occured, otherwise an instance of User who was blocked
     */
    public User blockUserById(int idForBlock) throws SQLException {
        //Connection connection = getConnection();
        User resultUser = null;
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("blockUserById1", "UPDATE USERS SET Isblocked = 1 WHERE Id_User = ? ");
            preparedStatement.setInt(1, idForBlock);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("ID USER: " + idForBlock + " IS BLOCKED");
            }

            preparedStatement = getPreparedStatementFromHashMap("blockUserById1", "SELECT * FROM USERS US " +
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
            preparedStatement.commit();

        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        } finally {
            try {
                //connection.setAutoCommit(true);
                //close(connection, preparedStatement);
                preparedStatement.close();
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

        return resultUser;
    }




    /**
     * Blocks a user by his id.
     *
     * @param email email of a user who should be blocked
     * @return Null if error occured, otherwise an instance of User who was blocked
     */
    public User blockUserByEmail(String email) throws SQLException {
        // Connection connection = getConnection();
        User resultUser = null;
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("blockUserByEmail1", "UPDATE USERS SET IS_BLOCKED = 1 WHERE E_MAIL = ? ");
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("USER WITH EMAIL " + email + " IS BLOCKED");
            }

            preparedStatement = getPreparedStatementFromHashMap("blockUserByEmail2", "SELECT * FROM USERS US " +
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
            preparedStatement.commit();

        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        } finally {
            try {
                //connection.setAutoCommit(true);
                preparedStatement.close();
                //close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }

        return resultUser;
    }


    /**
     * Checks if user with specified email exists in database.
     *
     * @param email email to check
     * @return true if user exists, false if doesn't
     */
    public boolean checkForExistingUserByEmail(String email) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("checkForExistingUserByEmail", "SELECT COUNT(Id_User) RES FROM USERS WHERE E_MAIL = ?");
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

        }  finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }


    /**
     * Get user role name by user role id.
     *
     * @param userRoleId user role id to return name for
     * @return null if there is no user role with this id, otherwise user role name
     */
    public String getUserRoleNameByUserRoleId(int userRoleId) throws SQLException {
        //Connection connection = getConnection();
        String result = null;
        PreparedStatementBlocker preparedStatement = null;
        try {

            preparedStatement =
                    getPreparedStatementFromHashMap("getUserRoleNameByUserRoleId", "SELECT NAME RES FROM USERROLE WHERE ID_USERROLE = ?");
            preparedStatement.setInt(1, userRoleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("RES");
            }

        } finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
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


    /**
     * Change the password for user.
     *
     * @param userId      User id to change password for.
     * @param newPassword New password for the user.
     * @return Instance of User if his password was changed succesfully, otherwise null.
     */
    public User changeUserPasswordById(int userId, String newPassword) throws SQLException {
        //Connection connection = getConnection();
        User resultUser = null;
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement =
                    getPreparedStatementFromHashMap("changeUserPasswordById1", "UPDATE USERS SET PASSWORD = ? WHERE Id_USER = ?");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            {//help
                System.out.println(" FOR ID USER: " + userId + " password was successfully changed");
            }

            preparedStatement = getPreparedStatementFromHashMap("changeUserPasswordById2", "SELECT *  FROM USERS US " +
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
            preparedStatement.commit();

        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            e.printStackTrace();
        } finally {
            try {
                //connection.setAutoCommit(true);
                preparedStatement.close();
                //close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return resultUser;
    }

    /**
     * Get information about all the routers, sum of occupied and sum of free ports on each router.
     *
     * @return ResultSet with routers(each row contains router id, sum of occupied, sum of free ports for this router)
     */
    public ReportGenerator getDevicesForReport(final String EXT) {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getDevicesForReport", "SELECT r.id ROUTER, SUM(P.Used) OCCUPIED, 60 - SUM(p.Used) FREE " +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                    "GROUP BY r.id ");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Devices", resultSet);
            } else if (EXT.equals("csv")) {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return reportGenerator;
    }

    /**
     * Get information about all the ports in system.
     *
     * @return ResultSet with ports (each row contains router id, port id, port used or not value)
     * @throws SQLException
     */
    public XLSReportGenerator getPortsForReport() throws SQLException {
        {//help
            System.out.println("getPortsForReport");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getPortsForReport", "SELECT R.Id ROUTER, P.Id PORT, P.Used USED " +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                    "Order By R.Id, P.Id ");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Ports", resultSet);
            {//help
                System.out.println("SUCCESS!!!!getPortsForReport");
            }
        } finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reportGenerator;
    }

    /**
     * Get information about all the cables in system.
     *
     * @return ResultSet with cables (each row contains cable id and instance service id, where this cable is connected to)
     * @throws SQLException
     */
    public XLSReportGenerator getCablesForReport() throws SQLException {
        {//help
            System.out.println("getCablesForReport");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getCablesForReport", "SELECT C.Id CABLE, Si.Id SERVICE_INSTANCE " +
                    "FROM (Cable C INNER JOIN Serviceinstance SI ON C.Id = Si.Id_Cable) " +
                    "ORDER BY C.Id");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Cables", resultSet);
            {//help
                System.out.println("SUCCESS!!!!getCablesForReport");
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reportGenerator;
    }


    /**
     * Get information about all the circuits in system.
     *
     * @return ResultSet with circuits(each row contains router id, port id, cable id, service instance id)
     */
    public XLSReportGenerator getCircuitsForReport() {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getCircuitsForReport", "SELECT r.ID ROUTER, p.Id PORT, c.ID CABLE, si.ID SERVICE_INSTANCE " +
                    "FROM ((ROUTER r INNER JOIN PORT p ON r.Id = P.Id_Router) INNER JOIN CABLE c ON p.Id = C.Id_Port) " +
                    "INNER JOIN SERVICEINSTANCE si ON C.Id = Si.Id_Cable ");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Circuits", resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //close(connection, preparedStatement);
                preparedStatement.close();
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }

        }
        return reportGenerator;
    }

    /**
     * Checks if there isn't such phone number in the system.
     *
     * @param phone Phone number to check.
     * @return true if phone number exists, false if doesn't
     */
    public boolean checkForPhoneUniq(String phone) {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = getPreparedStatementFromHashMap("checkForPhoneUniq", "SELECT COUNT(Id_User) RES FROM USERS WHERE PHONE = ?");
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
                preparedStatement.close();
                //close(connection, preparedStatement);
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
     * @throws java.sql.SQLException
     */
    public User getUserByLoginAndPassword(String login, String password) throws SQLException {
        {//help
            System.out.println("getUserByLoginAndPassword");
        }
        //Connection connection = getConnection();
        User user = null;
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getUserByLoginAndPassword", "SELECT * " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
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
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.Scenario
     */
    public int createServiceOrder(int userId, Scenario scenario, Integer idServiceInstance) throws SQLException {

        //default status ENTERING

        OrderStatus orderStatus = OrderStatus.ENTERING;
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int result = 0;
        {//help
            System.out.println("CREATE NEW ORDER!");
        }
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createServiceOrder1", "SELECT ID_ORDERSCENARIO FROM ORDERSCENARIO WHERE NAME = ?");
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

            preparedStatement = getPreparedStatementFromHashMap("createServiceOrder2", "SELECT ID_ORDERSTATUS FROM ORDERSTATUS WHERE NAME = ?");
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
                preparedStatement = getPreparedStatementFromHashMap("createServiceOrder3", "INSERT INTO SERVICEORDER(ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                        "VALUES(?,?,?,? )");
                preparedStatement.setInt(1, idOrderScenario);
                preparedStatement.setInt(2, idOrderStatus);
                preparedStatement.setDate(3, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
                preparedStatement.setInt(4, userId);
                {//help
                    System.out.println("NULL");
                }
            } else {
                preparedStatement = getPreparedStatementFromHashMap("createServiceOrder4", "INSERT INTO SERVICEORDER(ID_SRVICEINSTANCE, ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
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
            preparedStatement = getPreparedStatementFromHashMap("createServiceOrder5", "SELECT MAX(ID_SERVICEORDER) RES FROM SERVICEORDER");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                {//help
                    System.out.println("RETURN : " + resultSet.getInt("RES"));
                }
                result = resultSet.getInt("RES");
            }
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS! CREATE NEW ORDER!");
            }

        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    logger.error("ROLLBACK transaction Failed of creating new router");
                }
            }
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return result;
    }

    //KaspYar

    /**
     * Connects selected instance and selected user
     *
     * @param instanceId id of the instance
     * @param userId     id of the user
     * @throws java.sql.SQLException*
     */
    public void setUserForInstance(int instanceId, int userId) throws SQLException {
        {//help
            System.out.println("SET USER FOR INSTANCE!");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("setUserForInstance","UPDATE SERVICEINSTANCE " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
    }

    //KaspYar

    /**
     * Sets selected status for selected instance
     *
     * @param instanceId id of the instance
     * @param status     status of the instance
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.InstanceStatus
     */
    public void changeInstanceStatus(int instanceId, InstanceStatus status) throws SQLException {
        {//help
            System.out.println("CHANGE INSTANCE STATUS!");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("changeInstanceStatus", "UPDATE SERVICEINSTANCE " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }

    //KaspYar

    /**
     * Set selected status for selected order
     *
     * @param orderId     id of the order
     * @param orderStatus status of the task
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.OrderStatus
     */
    public void changeOrderStatus(int orderId, OrderStatus orderStatus) throws SQLException {
        {//help
            System.out.println("CHANGE ORDER STATUS");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("changeOrderStatus", "UPDATE SERVICEORDER " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }

    //KaspYar

    /**
     * Set selected status for selected task
     *
     * @param taskId     id of the task
     * @param taskStatus status of the task
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskStatus
     */
    public void changeTaskStatus(int taskId, TaskStatus taskStatus) throws SQLException {
        {//help
            System.out.println("CHANGE TASK STATUS");
        }
        // Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("changeTaskStatus", "UPDATE TASK " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }

    //KaspYar

    /**
     * Block instance when exist active task on it
     *
     * @param instanceId id of the instance
     * @param isBlocked  0 - set instance not blocked, 1 - set instance blocked
     * @throws java.sql.SQLException
     */
    public void setInstanceBlocked(int instanceId, int isBlocked) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        {//help
            System.out.println("SET INSTANCE BLOCKED!");
        }
        try {
            preparedStatement = getPreparedStatementFromHashMap("setInstanceBlocked", "UPDATE SERVICEINSTANCE " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }

    //KaspYar

    /**
     * Returns tasks for selected usergroup with seleceted status
     *
     * @param taskStatusId id of TaskStatus
     * @param userRoleId   id of UserRole
     * @return List<Task> of tasks
     * @throws java.sql.SQLException
     */
    public List<Task> getTasksByStatusAndRole(int taskStatusId, int userRoleId) throws SQLException {
        {//help
            System.out.println("getTasksByStatusAndRole");
        }
        ArrayList<Task> result = new ArrayList<Task>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getTasksByStatusAndRole", "SELECT T.ID_TASK, T.ID_USERROLE, T.ID_SERVICEORDER, T.ID_TASKSTATUS, TS.NAME TS_NAME, T.NAME T_NAME " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;
    }
    //KaspYar

    /**
     * returns all Services of Provider Location with certain id
     *
     * @param providerLocationId id of Provider Location
     * @return ArrayList<Service> of services
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public List<Service> getServicesByProviderLocationId(int providerLocationId) throws SQLException {
        {//help
            System.out.println("getServicesByProviderLocationId");
        }
        ArrayList<Service> result = new ArrayList<Service>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getServicesByProviderLocationId", "SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Creates new router
     *
     * @throws java.sql.SQLException
     */
    public void createRouter() throws SQLException {
        {//help
            System.out.println("CREATE ROUTER");
        }
        int amountsOfPorts = 60;
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createRouter1", "INSERT INTO ROUTER(ID) VALUES(null)");
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createRouter2", "SELECT MAX(ID) M FROM ROUTER");
            ResultSet resultSet = preparedStatement.executeQuery();
            int idRouter = 0;
            if (resultSet.next()) {
                idRouter = resultSet.getInt("M");
            }
            StringBuilder sb = new StringBuilder("INSERT ALL ");
            for (int i = 1; i <= amountsOfPorts; i++) sb.append("INTO PORT(ID_ROUTER) VALUES(" + idRouter + ") ");
            sb.append("SELECT * FROM DUAL");
            preparedStatement = getPreparedStatementFromHashMap("createRouter3", sb.toString());
            preparedStatement.executeUpdate();
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE NEW ROUTER");
            }
        } catch (SQLException e) {
            if (preparedStatement != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
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
     * @return found ServiceOrder
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceOrder
     */

    public ServiceOrder getServiceOrder(int taskId) throws SQLException {
        {//help
            System.out.println("getServiceOrder");
        }
        ServiceOrder result = null;
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getServiceOrder", "SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OST.NAME OST_NAME, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }


    //KaspYar

    /**
     * Connects selected instance and selected order
     *
     * @param instanceId id of the instance
     * @param orderId    id of the order
     * @throws java.sql.SQLException
     */
    public void setInstanceForOrder(int instanceId, int orderId) throws SQLException {
        {//help
            System.out.println("SET INSTANCE FOR ORDER");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("setInstanceForOrder", "UPDATE SERVICEORDER " +
                    "SET ID_SRVICEINSTANCE = ? " +
                    "WHERE ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, instanceId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
            {//help
                System.out.println("SUCCESS!!! SET INSTANCE FOR ORDER");
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }


    //KaspYar

    /**
     * Returns ArrayList of orders for selected user
     *
     * @param userId Id of the user
     * @return ArrayList of orders
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceOrder
     */

    public ArrayList<ServiceOrder> getOrders(int userId) throws SQLException {
        {//help
            System.out.println("GET ORDERS");
        }
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getOrders", "SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OST_NAME, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Returns ArrayList of instances for selected user
     *
     * @param userId Id of the user
     * @return ArrayList of instances
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceInstance
     */

    public ArrayList<ServiceInstance> getInstances(int userId) throws SQLException {
        {//help
            System.out.println("GET INSTANCES");
        }
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getInstances", "SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;

    }

    //KaspYar

    /**
     * Returns ArrayList of all orders
     *
     * @return ArrayList of ServiceOrder
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceOrder
     */
    public ArrayList<ServiceOrder> getAllOrders() throws SQLException {
        {//help
            System.out.println("GET ALL ORDERS");
        }
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {

            preparedStatement =
                    getPreparedStatementFromHashMap("getAllOrders", "SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OS_NAME, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;

    }
    //KaspYar

    /**
     * Returns ArrayList of all instances
     *
     * @return ArrayList of ServiceInstance
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceInstance
     */
    public ArrayList<ServiceInstance> getAllInstances() throws SQLException {
        {//help
            System.out.println("GET ALL INSTANCES");
        }
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getAllInstances", "SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;

    }

    //mystic function for XLS - generator
    public ResultSet reportTester() throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("reportTester", "SELECT * FROM USERS");
            resultSet = preparedStatement.executeQuery();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return resultSet;
    }

    //KaspYar

    /**
     * Returns list of all Provider Locations existing in the system
     *
     * @return List of Provider Locations
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ProviderLocation
     */
    public List<ProviderLocation> getProviderLocations() throws SQLException {
        {//
            System.out.println("getProviderLocations");
        }
        ArrayList<ProviderLocation> result = new ArrayList<ProviderLocation>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getProviderLocations", "SELECT * " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;
    }

    //vladmyr

    /**
     * Returns Service by its Id
     *
     * @param serviceId if of the Service
     * @return if exist - Service, otherwise - null
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public Service getServiceById(int serviceId) throws SQLException {
        {//help
            System.out.println("getServiceById");
        }
        Service service = null;
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getServiceById", "SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return service;

    }

    //KaspYar

    /**
     * return List<Services> of all Services
     *
     * @return List of Services
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public List<Service> getServices() throws SQLException {
        {//
            System.out.println("getServices");
        }
        ArrayList<Service> result = new ArrayList<Service>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement =
                    getPreparedStatementFromHashMap("getServices", "SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;
    }

    //KaspYar

    /**
     * Creates service location record in database from ServiceLocation object
     *
     * @param serviceLocation ServiceLocation object to write
     * @return id of the created ServiceLocation
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceLocation
     */

    public int createServiceLocation(ServiceLocation serviceLocation) throws SQLException {
        {//help
            System.out.println("CREATE SERVICE LOCATION!");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int res = 0;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createServiceLocation1", "INSERT INTO LOCATION(ADRESS, LONGITUDE, LATITUDE) VALUES(?,?,?)");
            preparedStatement.setString(1, serviceLocation.getLocationAddress());
            preparedStatement.setDouble(2, serviceLocation.getLocationLongitude());
            preparedStatement.setDouble(3, serviceLocation.getLocationLatitude());
            {//help
                System.out.println("Adress: " + serviceLocation.getLocationAddress() +
                        " Longitude: " + serviceLocation.getLocationLongitude() +
                        " Latitude: " + serviceLocation.getLocationLatitude());
            }
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createServiceLocation2", "SELECT MAX(ID) MAX_ID FROM LOCATION");
            int id = 0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) id = resultSet.getInt("MAX_ID");
            {//help
                System.out.println("MAX_ID: " + id);
            }
            preparedStatement = getPreparedStatementFromHashMap("createServiceLocation3", "INSERT INTO SERVICELOCATION(ID_LOCATION) VALUES (?)");
            preparedStatement.setInt(1, id);
            {//help
                System.out.println("INSERT INTO SERVICELOCATION(ID_LOCATION) VALUES (?) DONE");
            }
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createServiceLocation4", "SELECT MAX(ID) MAX_ID FROM SERVICELOCATION");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            {//help
                System.out.println("MAX_ID " + res);
            }
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE SERVICE LOCATION!");
            }
        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
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
     * @param serviceId       id of selected service
     * @return id of created instance
     * @throws java.sql.SQLException
     */

    public int createServiceInstance(int userId, ServiceLocation serviceLocation, int serviceId) throws SQLException {
        {//help
            System.out.println("CREATE SERVICE INSTANCE!");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int res = 0;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createServiceInstance1", "INSERT INTO SERVICEINSTANCE(ID_USER, ID_SERVICE_LOCATION, " +
                    "ID_SERVICE, SERVICE_INSTANCE_STATUS) " +
                    "VALUES (?,?,?, (SELECT SIS.ID FROM SERVICEINSTANCESTATUS SIS WHERE NAME = ? ) )");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, serviceLocation.getServiceLocationId());
            System.out.println("SERVICE LOC ID: " + serviceLocation.getServiceLocationId());
            System.out.println("Service ID: " + serviceId);
            preparedStatement.setInt(3, serviceId);
            preparedStatement.setString(4, InstanceStatus.PLANNED.toString());
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createServiceInstance2", "SELECT MAX(ID) MAX_ID FROM SERVICEINSTANCE");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS !! CREATE SERVICE INSTANCE!");
            }
        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
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
     * @throws java.sql.SQLException
     */
    public int createNewTask(int serviceOrderId, UserRole role, TaskName taskName) throws SQLException {
        {//help
            System.out.println("CREATE TASK");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int taskId = 0;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createNewTask1", "INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
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
            preparedStatement = getPreparedStatementFromHashMap("createNewTask2", "SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            {//help
                System.out.println("MAX_ID: " + taskId);
            }
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS!!! CREATE TASK");
            }
        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return taskId;
    }
//
//
//    /**
//     * Creates task with status FREE for installation engineer for selected service order
//     *
//     * @param serviceOrderId
//     * @return id of created task
//     */
//    public int createTaskForInstallation(int serviceOrderId) throws  SQLException{
//        {//help
//            System.out.println("CREATE TASK FOR INSTALLATION");
//        }
//        Connection connection = getConnection();
//        PreparedStatement preparedStatement = null;
//        int taskId = 0;
//        try {
//            connection.setAutoCommit(false);
//            preparedStatement = connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
//                    "VALUES ( " +
//                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
//                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
//                    "?, ?)");
//            {//HELP
//                System.out.println("taskStatus: " + TaskStatus.FREE.toString());
//                System.out.println("userRole: " + UserRoles.INSTALLATION_ENG.toString());
//                System.out.println("serviceOrderId " + serviceOrderId);
//                System.out.println("TaskName: " + TaskName.CONNECT_NEW_PERSON.toString());
//            }
//            preparedStatement.setString(1, TaskStatus.FREE.toString());
//            preparedStatement.setString(2, UserRoles.INSTALLATION_ENG.toString());
//            preparedStatement.setInt(3, serviceOrderId);
//            preparedStatement.setString(4, TaskName.CONNECT_NEW_PERSON.toString());
//            preparedStatement.executeUpdate();
//            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
//            {//help
//                System.out.println("MAX_ID: " + taskId);
//            }
//            connection.commit();
//            {//help
//                System.out.println("SUCCESS!!! CREATE TASK FOR INSTALLATION");
//            }
//        } catch (SQLException e) {
//            if (connection != null) {
//                System.err.print("Transaction is being rolled back");
//                try {
//                    connection.rollback();
//                } catch (SQLException e1) {
//                    e1.printStackTrace();
//                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
//                }
//            }
//            e.printStackTrace();
//            throw e;
//        } finally {
//            try {
//                close(connection, preparedStatement);
//            } catch (SQLException e) {
//                logger.info("Smth wrong with closing connection or preparedStatement!");
//                e.printStackTrace();
//            }
//        }
//        return taskId;
//    }
//
//    //KaspYar
//
//    /**
//     * Creates task with status FREE for provisioning engineer for selected service order
//     *
//     * @param serviceOrderId
//     * @return id of created task
//     */
//    public int createTaskForProvisioning(int serviceOrderId) throws SQLException{
//        {//help
//            System.out.println("CREATE TASK FOR PROVISIONING");
//        }
//        Connection connection = getConnection();
//        PreparedStatement preparedStatement = null;
//        int taskId = 0;
//        try {
//            connection.setAutoCommit(false);
//            preparedStatement = connection.prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
//                    "VALUES ( " +
//                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
//                    "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
//                    "?, ?)");
//            {//help
//                System.out.println("TaskStatus: " + TaskStatus.FREE.toString());
//                System.out.println("userRole: " + UserRoles.PROVISIONING_ENG.toString());
//                System.out.println("idServiceOrder " + serviceOrderId);
//                System.out.println("taskName: " + TaskName.CREATE_CIRCUIT.toString());
//            }
//            preparedStatement.setString(1, TaskStatus.FREE.toString());
//            preparedStatement.setString(2, UserRoles.PROVISIONING_ENG.toString());
//            preparedStatement.setInt(3, serviceOrderId);
//            preparedStatement.setString(4, TaskName.CREATE_CIRCUIT.toString());
//            preparedStatement.executeUpdate();
//            preparedStatement = connection.prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
//            connection.commit();
//            {//help
//                System.out.println("SUCCESS!!! CREATE TASK FOR PROVISIONING");
//            }
//        } catch (SQLException e) {
//        if (connection != null) {
//            System.err.print("Transaction is being rolled back");
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//                logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
//            }
//        }
//        e.printStackTrace();
//        throw e;
//    } finally {
//        try {
//            close(connection, preparedStatement);
//        } catch (SQLException e) {
//            logger.info("Smth wrong with closing connection or preparedStatement!");
//            e.printStackTrace();
//        }
//    }
//        return taskId;
//    }

    //KaspYar


    /**
     * Returns List of task with status FREE and PROCESSING for selected usergroup
     *
     * @param userRoleId id of the usergroup
     * @return List of tasks
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskStatus
     * @see UserRole
     */
    public List<Task> getFreeAndProcessingTasksByUserRoleId(int userRoleId) throws SQLException {
        {//help
            System.out.println("getFreeAndProcessingTasksByUserRoleId");
            System.out.println("UserRoleId:" + userRoleId);
        }
        ArrayList<Task> result = new ArrayList<Task>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getFreeAndProcessingTasksByUserRoleId", "SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, T.ID_SERVICEORDER, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
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
     *
     * @param serviceOrderId id of service order to take service instance from
     * @throws java.sql.SQLException
     */
    public void createPortAndCableAndAssignToServiceInstance(int serviceOrderId) throws SQLException {
        {//help
            System.out.println("createPortAndCableAndAssignToServiceInstance");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        int portId = 0;
        int cableId = 0;
        try {
            preparedStatement.setAutoCommit(false);
            preparedStatement = getPreparedStatementFromHashMap("createPortAndCableAndAssignToServiceInstance1", "SELECT MIN(ID) PORT_ID FROM PORT WHERE USED = 0");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) portId = resultSet.getInt("PORT_ID");
            preparedStatement = getPreparedStatementFromHashMap("createPortAndCableAndAssignToServiceInstance2", "INSERT INTO CABLE(ID_PORT) VALUES(?)");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createPortAndCableAndAssignToServiceInstance3", "SELECT MAX(ID) CABLE_ID FROM CABLE");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) cableId = resultSet.getInt("CABLE_ID");
            preparedStatement = getPreparedStatementFromHashMap("createPortAndCableAndAssignToServiceInstance4", "UPDATE SERVICEINSTANCE " +
                    "SET ID_CABLE = ? " +
                    "WHERE ID = (SELECT ID_SRVICEINSTANCE FROM SERVICEORDER WHERE ID_SERVICEORDER = ?)");
            preparedStatement.setInt(1, cableId);
            preparedStatement.setInt(2, serviceOrderId);
            preparedStatement.executeUpdate();
            preparedStatement = getPreparedStatementFromHashMap("createPortAndCableAndAssignToServiceInstance5", "UPDATE PORT SET USED = 1 WHERE ID = ?");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            preparedStatement.commit();
            {//help
                System.out.println("SUCCESS!!! createPortAndCableAndAssignToServiceInstance");
            }
        } catch (SQLException e) {
            if (preparedStatement.getConnection() != null) {
                System.err.print("Transaction is being rolled back");
                try {
                    preparedStatement.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
    }

    //KaspYar

    /**
     * Checks if free ports exist
     *
     * @return True if a free port exists, otherwise false
     * @throws java.sql.SQLException
     */
    public boolean freePortExists() throws SQLException {
        {//help
            System.out.println("freePortExists");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = getPreparedStatementFromHashMap("freePortExists", "SELECT COUNT(*) AM FROM PORT WHERE USED = ?");
            preparedStatement.setInt(1, 0);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = (resultSet.getInt("AM") > 0);
            {//help
                System.out.println("SUCCESS!!! freePortExists");
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    //KaspYar

    /**
     * Returns scenario type for this service
     *
     * @param serviceOrderId id of service
     * @return scenario of service
     * @throws java.sql.SQLException
     */
    public Scenario getOrderScenario(int serviceOrderId) throws SQLException {
        {//help
            System.out.println("getOrderScenario");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        Scenario result = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getOrderScenario", "SELECT OS.NAME " +
                    "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
                    "WHERE SO.ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, serviceOrderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = Scenario.valueOf(resultSet.getString("NAME"));
            {//help
                System.out.println("SUCCESS!!! getOrderScenario");
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Returns status of task
     *
     * @param taskId id of the task
     * @return Status of this task
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskStatus
     */
    public TaskStatus getTaskStatus(int taskId) throws SQLException {
        {//help
            System.out.println("getTaskStatus");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        TaskStatus result = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getTaskStatus", "SELECT TS.NAME " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }


    /**
     * Returns task with given id PK
     *
     * @param taskId id of the task
     * @return task object with selected id
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Task
     */
    public Task getTaskById(int taskId) throws SQLException {
        {//help
            System.out.println("getTaskById");
        }
        Task task = null;
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getTaskById", "SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, " +
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
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return task;
    }

    /**
     * Creates ResultSet to generate report on most profitable router
     *
     * @return ResultSet of the sql request
     * @throws java.sql.SQLException
     * @see java.sql.ResultSet
     */
    public XLSReportGenerator getMostProfitableRouterForReport() throws SQLException {
        {//help
            System.out.println("getMostProfitableRouterForReport");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getMostProfitableRouterForReport", "SELECT P.ID_ROUTER, SUM(S.PRICE) PROFIT " +
                    "FROM SERVICE S INNER JOIN ( " +
                    "  SERVICEINSTANCE SI INNER JOIN ( " +
                    "    CABLE C INNER JOIN PORT P ON C.ID_PORT = P.ID)  " +
                    "  ON SI.ID_CABLE = C.ID) " +
                    "ON  S.ID = SI.ID_SERVICE " +
                    "GROUP BY P.ID_ROUTER ORDER BY PROFIT DESC ");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Most Profitable Router", resultSet);
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getMostProfitableRouterForReport");
        }
        return reportGenerator;
    }


    /**
     * Sets selected service for selected task for scenario MODIFY
     *
     * @param taskId    selected task
     * @param serviceId id of the service to set for task
     * @throws java.sql.SQLException
     */
    public void setServiceForTask(int taskId, int serviceId) throws SQLException {
        {//help
            System.out.println("setServiceForTask");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("setServiceForTask", "INSERT INTO TOMODIFY(ID_TASK, ID_SERVICE) " +
                    "VALUES(?,?)");
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, serviceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!setServiceForTask");
        }
        return;
    }

    /**
     * Returns services of selected ids
     *
     * @param arrayServiceId array of service ids
     * @return List of Services
     * @throws java.sql.SQLException
     * @see java.util.List
     * @see com.naukma.cauliflower.entities.Service
     */
    public List<Service> getServiceById(int[] arrayServiceId) throws SQLException {
        {//help
            System.out.println("getServiceById(arr [])");
        }
        ArrayList<Service> result = new ArrayList<Service>();
        //Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("" +
                "SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID WHERE S.ID = ? ");
        for (int i = 0; i < arrayServiceId.length - 1; i++) {
            stringBuilder.append(" OR S.ID = ? ");
        }
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getServiceById", stringBuilder.toString());
            for (int i = 0; i < arrayServiceId.length; i++) {
                preparedStatement.setInt(i + 1, arrayServiceId[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result.add(new Service(
                        resultSet.getInt("ID_SERVICE_TYPE"),
                        resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("SPEED"),
                        resultSet.getInt("ID_PROVIDER_LOCATION"),
                        resultSet.getInt("ID"),
                        resultSet.getDouble("PRICE")
                ));
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getMostProfitableRouterForReport");
        }
        result.trimToSize();
        {//help
            System.out.println("SUCCESS!!! getServiceById(arr [])");
        }
        return result;
    }

    /**
     * Method changes the service for service instance taking a new service from a task
     *
     * @param taskId            Task to take service from
     * @param serviceInstanceId Service Instance that will be changed
     * @throws java.sql.SQLException
     */
    public void changeServiceForServiceInstance(int taskId, int serviceInstanceId) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("changeServiceForServiceInstance", "UPDATE SERVICEINSTANCE " +
                    "SET ID_SERVICE = (SELECT ID_SERVICE " +
                    "FROM TASK T INNER JOIN TOMODIFY TMOD ON TMOD.ID_TASK = T.ID " +
                    "WHERE T.ID = ?) " +
                    "WHERE ID = ?");
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, serviceInstanceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }


    /**
     * Checks if service instance is blocked
     *
     * @param serviceInstanceId if of the instance
     * @return true if instance is blocked, otherwise - false
     * @throws java.sql.SQLException
     */
    public boolean isInstanceBlocked(int serviceInstanceId) throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = getPreparedStatementFromHashMap("isInstanceBlocked", "SELECT HAS_ACTIVE_TASK IS_BLOCKED FROM SERVICEINSTANCE WHERE ID = ? ");
            preparedStatement.setInt(1, serviceInstanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                result = (resultSet.getInt("IS_BLOCKED") == 1);
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;

    }

    public int getUserRoleIdByUserRoleName(String userRole){
        return 0;
    }




    /**
     * return List<Services> of all Services
     *
     * @return List of Services
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public List<User> getCustomers() throws SQLException {
        final int CUSTOMER = 1;
        {//
            System.out.println("getCustomers");
        }
        ArrayList<User> result = new ArrayList<User>();
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        try {
            String query = "SELECT * " +
                    "FROM USERS " +
                    "WHERE ID_USERROLE = ?";
            {//
                System.out.println(query);
            }
            preparedStatement = getPreparedStatementFromHashMap("getCustomers", query);
            preparedStatement.setInt(1, CUSTOMER);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new User(resultSet.getInt("ID_USER"),
                        resultSet.getInt("ID_USERROLE"),
                        "CUSTOMER",
                        resultSet.getString("E_MAIL"),
                        resultSet.getString("F_NAME"),
                        resultSet.getString("L_NAME"),
                        resultSet.getString("PHONE"),
                        ((resultSet.getString("IS_BLOCKED") == "1")? true: false)));
            }
            {//
                System.out.println("SUCCESS!!! getServices");
            }
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;

    }

    /**---------------------------------------------------------------------END KASPYAR---------------------------------------------------------------------**/


    /**
     * ---------------------------------------------------------------------IGOR---------------------------------------------------------------------*
     */


    /**
     * Prepare ResultSet  to generate report on used routers and port capacity
     *
     * @return ResultSet for sql request
     * @throws java.sql.SQLException
     */
    public XLSReportGenerator getUsedRoutersAndCapacityOfPorts() throws SQLException {
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getUsedRoutersAndCapacityOfPorts", "SELECT r.id ROUTER, 60 - SUM(P.Used) FREE,  SUM(p.Used) OCCUPIED, " +
                    "ROUND((SUM(p.Used))/( 60 - SUM(P.Used)), 2) UTILIZATION\n" +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) \n" +
                    "GROUP BY r.id ");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Routers and capacity of ports", resultSet);
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getMostProfitableRouterForReport");
        }
        return reportGenerator;
    }

    /**
     * Prepare ResultSet  to generate report on the most profitable router
     *
     * @return ResultSet for sql request
     * @throws java.sql.SQLException
     */

    public XLSReportGenerator getProfitabilityByMonth() throws SQLException {
        {//help
            System.out.println("getProfitabilityByMonth");
        }
        //Connection connection = getConnection();
        PreparedStatementBlocker preparedStatement = null;
        ResultSet resultSet = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getProfitabilityByMonth", "SELECT P.ID_ROUTER, SUM(S.PRICE) PROFIT " +
                    "FROM SERVICE S INNER JOIN ( " +
                    "  SERVICEINSTANCE SI INNER JOIN ( " +
                    "    CABLE C INNER JOIN PORT P ON C.ID_PORT = P.ID)  " +
                    "  ON SI.ID_CABLE = C.ID) " +
                    "ON  S.ID = SI.ID_SERVICE " +
                    "GROUP BY P.ID_ROUTER ");
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Profitability by month", resultSet);
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getProfitabilityByMonth");
        }
        return reportGenerator;
    }


    /**
     * Prepare ResultSet  to generate report on service orders by date
     *
     * @param scenario     order scenario
     * @param sqlEndDate   date to start searching orders
     * @param sqlStartDate date to end searching orders
     * @return ResultSet for sql request
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.Scenario
     */

    public XLSReportGenerator getOrdersPerPeriod(Scenario scenario, java.sql.Date sqlStartDate, java.sql.Date sqlEndDate) throws SQLException {

        {//help
            System.out.println("getOrdersPerPeriod");
        }
        //Connection connection = getConnection();
        ResultSet resultSet = null;
        PreparedStatementBlocker preparedStatement = null;
        XLSReportGenerator reportGenerator = null;
        try {
            preparedStatement = getPreparedStatementFromHashMap("getOrdersPerPeriod", "SELECT OS.NAME SCENARIO, COUNT(*) AMOUNT " +
                    "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
                    "WHERE OS.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? " +
                    "GROUP BY OS.NAME ");

            preparedStatement.setString(1, scenario.toString());
            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlEndDate);
            resultSet = preparedStatement.executeQuery();
            reportGenerator = new XLSReportGenerator("Get " + scenario + " orders", resultSet);
        } finally {
            try {
                preparedStatement.close();
                //close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getOrdersPerPeriod");
        }
        return reportGenerator;
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


    /**
     * Breaks circuit
     *
     * @param serviceOrderId id of order connected with circuit
     * @throws java.sql.SQLException
     */
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

