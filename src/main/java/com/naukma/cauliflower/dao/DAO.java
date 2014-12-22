package com.naukma.cauliflower.dao;


import com.naukma.cauliflower.entities.*;
import com.naukma.cauliflower.info.CauliflowerInfo;
import com.naukma.cauliflower.reports.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public class DAO {
    public static final DAO INSTANCE = new DAO();
    private static final Logger logger = Logger.getLogger(String.class);
    private DataSource dataSource;
    private static final String BD_JNDI = "jdbc/oraclesource";

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

    /**
     * Returns id of specific UserRole
     *
     * @param userRole UserRole to get id for
     * @return id of specific UserRole in database
     * @throws SQLException
     */
    public int getUserRoleIdFor(UserRole userRole) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 0;
        try {
            preparedStatement = connection.prepareStatement("SELECT Id_UserRole RES FROM USERROLE WHERE NAME = ?");
            preparedStatement.setString(1, userRole.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("RES");
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
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
    public int createUser(User us, String password) throws SQLException {
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
                    logger.error("ROLLBACK transaction Failed of creating new user");
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try {
                //connection.setAutoCommit(true);
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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

        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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

        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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
            throw e;

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


    /**
     * Blocks a user by his id.
     *
     * @param email email of a user who should be blocked
     * @return Null if error occured, otherwise an instance of User who was blocked
     */
    public User blockUserByEmail(String email) throws SQLException {
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
                    //e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed");
                }
            }
            throw e;
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


    /**
     * Checks if user with specified email exists in database.
     *
     * @param email email to check
     * @return true if user exists, false if doesn't
     */
    public boolean checkForExistingUserByEmail(String email) throws SQLException {
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

        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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

        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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
                    //e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating new service location");
                }
            }
            throw e;
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

    /**
     * Get information about all the routers, sum of occupied and sum of free ports on each router.
     *
     * @return ResultSet with routers(each row contains router id, sum of occupied, sum of free ports for this router)
     */
    public ReportGenerator getDevicesForReport(final String EXT) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        int amountOfPorts = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.prepareStatement("SELECT 'ROUTER-'||r.id ROUTER, SUM(P.Used) OCCUPIED, "+amountOfPorts+" - SUM(p.Used) FREE " +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                    "GROUP BY r.id ORDER BY R.ID ASC");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Devices", resultSet);
            } else if (EXT.equals("csv")) {
                reportGenerator = new CSVReportGenerator(resultSet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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
    public ReportGenerator getPortsForReport(final String EXT) throws SQLException {
        {//help
            System.out.println("getPortsForReport");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.prepareStatement("SELECT 'ROUTER-'||R.ID ROUTER, " +
                    "'ROUTER-'||R.ID||'-'||MOD(P.ID, "+portsQuantity +") PORT,  " +
                    "CASE P.USED WHEN 1 THEN 'YES' ELSE 'NO' END USED " +
                    "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                    "ORDER BY R.ID, MOD(P.ID,"+portsQuantity+") ");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Ports", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
            {//help
                System.out.println("SUCCESS!!!!getPortsForReport");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
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
    public ReportGenerator getCablesForReport(final String EXT) throws SQLException {
        {//help
            System.out.println("getCablesForReport");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT 'CABLE-'||C.Id CABLE, L.ADRESS SERVICE_INSTANCE_ADRESS " +
                    "FROM (Cable C INNER JOIN (SERVICEINSTANCE si inner join " +
                    "(SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                    "ON SI.ID_SERVICE_LOCATION = SL.ID ) " +
                    "ON C.Id = Id_Cable) " +
                    "ORDER BY C.ID, l.adress ");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Cables", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
            {//help
                System.out.println("SUCCESS!!!!getCablesForReport");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
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
    public ReportGenerator getCircuitsForReport(final String EXT) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT 'ROUTER-'||r.ID ROUTER, " +
                            "'ROUTER-'||r.ID||'-'||MOD(p.Id, "+portsQuantity+") PORT, " +
                            "'CABLE-'||c.ID CABLE, L.ADRESS SERVICE_INSTANCE_ADRESS  " +
                            "FROM ((ROUTER r INNER JOIN PORT p ON r.Id = P.Id_Router) " +
                            "INNER JOIN CABLE c ON p.Id = C.Id_Port) INNER JOIN (SERVICEINSTANCE SI " +
                            "INNER JOIN (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                            "ON SI.ID_SERVICE_LOCATION = SL.ID ) " +
                            "ON C.Id = Si.Id_Cable " +
                            "ORDER BY R.ID, MOD(P.ID,"+portsQuantity+") ASC");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Circuits", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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
    public boolean checkForPhoneUniq(String phone) throws SQLException {
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

        } finally {
            try {
                close(connection, preparedStatement);
                //if (!preparedStatement.isClosed()) preparedStatement.close();
                //if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
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
        Connection connection = getConnection();
        User user = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT U.*, UR.* " +
                            "FROM USERS U, USERROLE UR " +
                            "WHERE U.ID_USERROLE = UR.ID_USERROLE(+) AND E_MAIL = ? AND PASSWORD = ? ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int result = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("SELECT ID_ORDERSCENARIO FROM ORDERSCENARIO WHERE NAME = ?");
            preparedStatement.setString(1, scenario.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int idOrderScenario = 0;
            if (resultSet.next()) {
                idOrderScenario = resultSet.getInt("ID_ORDERSCENARIO");
            }
            preparedStatement = connection.
                    prepareStatement("SELECT ID_ORDERSTATUS FROM ORDERSTATUS WHERE NAME = ?");
            preparedStatement.setString(1, orderStatus.toString());
            int idOrderStatus = 0;
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idOrderStatus = resultSet.getInt("ID_ORDERSTATUS");
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date d = gregorianCalendar.getTime();
            if (idServiceInstance == null) {
                preparedStatement = connection.
                        prepareStatement("INSERT INTO SERVICEORDER(ID_ORDERSCENARIO," +
                                "ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                                "VALUES(?,?,?,? )");
                preparedStatement.setInt(1, idOrderScenario);
                preparedStatement.setInt(2, idOrderStatus);
                preparedStatement.setDate(3, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
                preparedStatement.setInt(4, userId);
            } else {
                preparedStatement = connection.
                        prepareStatement("INSERT INTO SERVICEORDER(ID_SRVICEINSTANCE, " +
                                "ID_ORDERSCENARIO,ID_ORDERSTATUS, OUR_DATE, ID_USER) " +
                                "VALUES(?, ?,? ,?,?)");
                preparedStatement.setInt(1, idServiceInstance.intValue());
                preparedStatement.setInt(2, idOrderScenario);
                preparedStatement.setInt(3, idOrderStatus);
                preparedStatement.setDate(4, new java.sql.Date(d.getYear(), d.getMonth(), d.getDay()));
                preparedStatement.setInt(5, userId);
            }
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID_SERVICEORDER) RES FROM SERVICEORDER");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("RES");
            }
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    logger.error("ROLLBACK transaction Failed of creating new router");
                }
            }
            throw e;
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEINSTANCE " +
                            "SET ID_USER = ? " +
                            "WHERE ID = ?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, instanceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEINSTANCE " +
                            "SET SERVICE_INSTANCE_STATUS = (SELECT ID " +
                            "FROM SERVICEINSTANCESTATUS " +
                            "WHERE NAME = ?) " +
                            "WHERE ID = ?");
            preparedStatement.setString(1, status.toString());
            preparedStatement.setInt(2, instanceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEORDER " +
                    "SET ID_ORDERSTATUS = (SELECT ID_ORDERSTATUS " +
                    "FROM ORDERSTATUS " +
                    "WHERE NAME = ?) " +
                    "WHERE ID_SERVICEORDER = ?");
            preparedStatement.setString(1, orderStatus.toString());
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE TASK " +
                    "SET ID_TASKSTATUS = (SELECT ID_TASKSTATUS " +
                    "FROM TASKSTATUS " +
                    "WHERE NAME = ?) " +
                    "WHERE ID_TASK = ?");
            preparedStatement.setString(1, taskStatus.toString());
            preparedStatement.setInt(2, taskId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEINSTANCE " +
                    "SET HAS_ACTIVE_TASK = ? " +
                    "WHERE ID = ?");
            preparedStatement.setInt(1, isBlocked);
            preparedStatement.setInt(2, instanceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT T.ID_TASK, T.ID_USERROLE, T.ID_SERVICEORDER, " +
                            "T.ID_TASKSTATUS, TS.NAME TS_NAME, T.NAME T_NAME " +
                            "FROM TASK T, TASKSTATUS TS " +
                            "WHERE T.ID_TASKSTATUS = TS.ID_TASKSTATUS(+) " +
                            "AND T.ID_TASKSTATUS = ? AND T.ID_USERROLE = ?");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, " +
                            "L.LONGITUDE, L.LATITUDE, " +
                            "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                            "FROM SERVICE S, SERVICETYPE ST, LOCATION L " +
                            "WHERE  S.ID_SERVICE_TYPE = ST.ID(+) " +
                            "AND S.ID_PROVIDER_LOCATION = L.ID(+) " +
                            "AND S.ID_PROVIDER_LOCATION = ?");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        int amountsOfPorts = CauliflowerInfo.PORTS_QUANTITY;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("INSERT INTO ROUTER(ID) VALUES(null)");
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID) M FROM ROUTER");
            ResultSet resultSet = preparedStatement.executeQuery();
            int idRouter = 0;
            if (resultSet.next()) {
                idRouter = resultSet.getInt("M");
            }
            //this object creates sql querry to create 60 ports
            StringBuilder sb = new StringBuilder("INSERT ALL ");
            for (int i = 1; i <= amountsOfPorts; i++) sb.append("INTO PORT(ID_ROUTER) VALUES(" + idRouter + ") ");
            sb.append("SELECT * FROM DUAL");
            preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
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
     * @return found ServiceOrder
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceOrder
     */

    public ServiceOrder getServiceOrder(int taskId) throws SQLException {
        ServiceOrder result = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OST.NAME OST_NAME, " +
                            "SO.ID_SRVICEINSTANCE, SO.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, " +
                            "SO.OUR_DATE, SO.ID_USER " +
                            "FROM TASK T, SERVICEORDER SO,ORDERSTATUS OST,ORDERSCENARIO OSC " +
                            "WHERE T.ID_SERVICEORDER = SO.ID_SERVICEORDER(+) " +
                            "AND SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS(+) " +
                            "AND SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO(+) " +
                            "AND T.ID_TASK = ? ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEORDER " +
                            "SET ID_SRVICEINSTANCE = ? " +
                            "WHERE ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, instanceId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        System.out.println("here");
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OST_NAME, " +
                            "SO.ID_SRVICEINSTANCE, OSC.ID_ORDERSCENARIO, " +
                            "OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                            "FROM SERVICEORDER SO,ORDERSTATUS OS,ORDERSCENARIO OSC " +
                            "WHERE SO.ID_ORDERSTATUS = OS.ID_ORDERSTATUS(+) " +
                            "AND SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO(+) " +
                            "AND SO.ID_USER = ? ORDER BY ID_SERVICEORDER DESC ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                            "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.HAS_ACTIVE_TASK, " +
                            "L.ADRESS,L.LATITUDE, L.LONGITUDE, SIS.NAME " +
                            "FROM SERVICEINSTANCE SI ,SERVICELOCATION SL, " +
                            "LOCATION L, SERVICEINSTANCESTATUS SIS " +
                            "WHERE SI.ID_SERVICE_LOCATION = SL.ID(+) " +
                            "AND SL.ID_LOCATION = L.ID(+) " +
                            "AND SI.SERVICE_INSTANCE_STATUS = SIS.ID(+) " +
                            "AND ID_USER = ?");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection.
                    prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OS.NAME OS_NAME, " +
                            "SO.ID_SRVICEINSTANCE, OSC.ID_ORDERSCENARIO, " +
                            "OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                            "FROM SERVICEORDER SO, ORDERSTATUS OS, ORDERSCENARIO OSC " +
                            "WHERE SO.ID_ORDERSTATUS = OS.ID_ORDERSTATUS(+) " +
                            "AND SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO(+)");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<ServiceInstance> result = new ArrayList<ServiceInstance>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT SI.ID, SI.ID_USER, SI.ID_SERVICE_LOCATION, SI.ID_SERVICE, " +
                            "SI.SERVICE_INSTANCE_STATUS, SI.ID_CABLE, SI.HAS_ACTIVE_TASK, " +
                            "L.ADRESS,L.LATITUDE, L.LONGITUDE, SIS.NAME " +
                            "FROM SERVICEINSTANCE SI,SERVICELOCATION SL, " +
                            "LOCATION L ,SERVICEINSTANCESTATUS SIS " +
                            "WHERE SI.ID_SERVICE_LOCATION = SL.ID(+) " +
                            "AND SL.ID_LOCATION = L.ID(+) " +
                            "AND SI.SERVICE_INSTANCE_STATUS = SIS.ID(+) ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
     * Returns list of all Provider Locations existing in the system
     *
     * @return List of Provider Locations
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ProviderLocation
     */
    public List<ProviderLocation> getProviderLocations() throws SQLException {
        ArrayList<ProviderLocation> result = new ArrayList<ProviderLocation>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT * " +
                            "FROM PROVIDERLOCATION PL, LOCATION L " +
                            "WHERE PL.ID_LOCATION = L.ID(+) ");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new ProviderLocation(resultSet.getInt("ID"),
                        resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE")));
            }
        } finally {
            try {
                close(connection, preparedStatement);
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
        Service service = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                            "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                            "FROM PROVIDERLOCATION PL, LOCATION L, SERVICE S, SERVICETYPE ST " +
                            "WHERE PL.ID_LOCATION = L.ID(+) " +
                            "AND S.ID_PROVIDER_LOCATION = PL.ID(+) " +
                            "AND S.ID_SERVICE_TYPE = ST.ID(+) " +
                            "AND  S.ID = ? ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        ArrayList<Service> result = new ArrayList<Service>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                            "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                            "FROM PROVIDERLOCATION PL, LOCATION L, SERVICE S, SERVICETYPE ST " +
                            "WHERE PL.ID_LOCATION = L.ID(+) " +
                            "AND S.ID_SERVICE_TYPE = ST.ID(+) " +
                            "AND  S.ID_PROVIDER_LOCATION = PL.ID(+)");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int res = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("INSERT INTO LOCATION(ADRESS, LONGITUDE, LATITUDE) VALUES(?,?,?)");
            preparedStatement.setString(1, serviceLocation.getLocationAddress());
            preparedStatement.setDouble(2, serviceLocation.getLocationLongitude());
            preparedStatement.setDouble(3, serviceLocation.getLocationLatitude());
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID) MAX_ID FROM LOCATION");
            int id = 0;
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) id = resultSet.getInt("MAX_ID");
            preparedStatement = connection.
                    prepareStatement("INSERT INTO SERVICELOCATION(ID_LOCATION) VALUES (?)");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID) MAX_ID FROM SERVICELOCATION");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
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
     * @param serviceId       id of selected service
     * @return id of created instance
     * @throws java.sql.SQLException
     */

    public int createServiceInstance(int userId, ServiceLocation serviceLocation, int serviceId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int res = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("INSERT INTO SERVICEINSTANCE(ID_USER, ID_SERVICE_LOCATION, " +
                            "ID_SERVICE, SERVICE_INSTANCE_STATUS) " +
                            "VALUES (?,?,?, (SELECT SIS.ID FROM SERVICEINSTANCESTATUS SIS WHERE NAME = ? ) )");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, serviceLocation.getServiceLocationId());
            preparedStatement.setInt(3, serviceId);
            preparedStatement.setString(4, InstanceStatus.PLANNED.toString());
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID) MAX_ID FROM SERVICEINSTANCE");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) res = resultSet.getInt("MAX_ID");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
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
     * @param taskStatus     status of the task
     * @return id of created task
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskStatus
     * @see com.naukma.cauliflower.dao.TaskName
     */
    public int createNewTask(int serviceOrderId, UserRole role, TaskName taskName, TaskStatus taskStatus) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("INSERT INTO TASK(ID_TASKSTATUS, ID_USERROLE, ID_SERVICEORDER, NAME) " +
                            "VALUES ( " +
                            "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME = ?), " +
                            "(SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?), " +
                            "?, ?)");
            preparedStatement.setString(1, taskStatus.toString());
            preparedStatement.setString(2, role.toString());
            preparedStatement.setInt(3, serviceOrderId);
            preparedStatement.setString(4, taskName.toString());
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID_TASK) TASK_ID FROM TASK");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) taskId = resultSet.getInt("TASK_ID");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
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
     * Returns List of task with status FREE and PROCESSING for selected usergroup
     *
     * @param userRoleId id of the usergroup
     * @return List of tasks
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.dao.TaskStatus
     * @see UserRole
     */
    public List<Task> getFreeAndProcessingTasksByUserRoleId(int userRoleId) throws SQLException {
        ArrayList<Task> result = new ArrayList<Task>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, T.ID_SERVICEORDER, " +
                            "T.NAME, TS.NAME TS_NAME " +
                            "FROM TASK T ,TASKSTATUS TS WHERE T.ID_TASKSTATUS = TS.ID_TASKSTATUS(+) " +
                            "AND ID_USERROLE = ? AND ((TS.NAME = ?) OR (TS.NAME = ?))");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
     * Creates a cable, assigns a free port to it and then assigns this cable to instance associated with service order
     *
     * @param serviceOrderId id of service order to take service instance from
     * @throws java.sql.SQLException
     */
    public void createPortAndCableAndAssignToServiceInstance(int serviceOrderId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int portId = 0;
        int cableId = 0;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.
                    prepareStatement("SELECT MIN(ID) PORT_ID FROM PORT WHERE USED = 0");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) portId = resultSet.getInt("PORT_ID");
            preparedStatement = connection.
                    prepareStatement("INSERT INTO CABLE(ID_PORT) VALUES(?)");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("SELECT MAX(ID) CABLE_ID FROM CABLE");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) cableId = resultSet.getInt("CABLE_ID");
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEINSTANCE " +
                            "SET ID_CABLE = ? " +
                            "WHERE ID = (SELECT ID_SRVICEINSTANCE FROM SERVICEORDER WHERE ID_SERVICEORDER = ?)");
            preparedStatement.setInt(1, cableId);
            preparedStatement.setInt(2, serviceOrderId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.
                    prepareStatement("UPDATE PORT SET USED = 1 WHERE ID = ?");
            preparedStatement.setInt(1, portId);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                logger.error("Transaction is being rolled back");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("ROLLBACK transaction Failed of creating createPortAndCableAndAssignToServiceInstance");
                }
            }
            e.printStackTrace();
            throw e;
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
     * Checks if free ports exist
     *
     * @return True if a free port exists, otherwise false
     * @throws java.sql.SQLException
     */
    public boolean freePortExists() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT COUNT(*) AM FROM PORT WHERE USED = ?");
            preparedStatement.setInt(1, 0);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = (resultSet.getInt("AM") > 0);
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        Scenario result = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT OS.NAME " +
                            "FROM SERVICEORDER SO, ORDERSCENARIO OS " +
                            "WHERE SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO(+) " +
                            "AND SO.ID_SERVICEORDER = ? ");
            preparedStatement.setInt(1, serviceOrderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = Scenario.valueOf(resultSet.getString("NAME"));
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        TaskStatus result = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT TS.NAME " +
                            "FROM TASK T , TASKSTATUS TS " +
                            "WHERE T.ID_TASKSTATUS = TS.ID_TASKSTATUS(+) " +
                            "AND T.ID_TASK =?");
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = TaskStatus.valueOf(resultSet.getString("NAME"));
            }
        } finally {
            try {
                close(connection, preparedStatement);
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
        Task task = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT T.ID_TASK, T.ID_TASKSTATUS, T.ID_USERROLE, " +
                            "T.ID_SERVICEORDER, T.NAME, TS.NAME TS_NAME " +
                            "FROM TASK T ,TASKSTATUS TS " +
                            "WHERE  T.ID_TASKSTATUS = TS.ID_TASKSTATUS(+) " +
                            "AND ID_TASK = ? ");
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
        } finally {
            try {
                close(connection, preparedStatement);
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
    public ReportGenerator getMostProfitableRouterForReport(final String EXT) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, SUM(S.PRICE) PROFIT " +
                            "FROM SERVICE S , SERVICEINSTANCE SI, CABLE C ,PORT P  " +
                            "WHERE S.ID = SI.ID_SERVICE(+) AND SI.ID_CABLE = C.ID(+) AND C.ID_PORT = P.ID(+) " +
                            "GROUP BY P.ID_ROUTER " +
                            "ORDER BY PROFIT DESC ");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Most Profitable Router", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            logger.warn("Can't create ReportGenerator!");
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("INSERT INTO TOMODIFY(ID_TASK, ID_SERVICE) VALUES(?,?)");
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, serviceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return;
    }



    /**
     * Method changes the service for service instance taking a new service from a task
     *
     * @param taskId            Task to take service from
     * @param serviceInstanceId Service Instance that will be changed
     * @throws java.sql.SQLException
     */
    public void changeServiceForServiceInstance(int taskId, int serviceInstanceId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.
                    prepareStatement("UPDATE SERVICEINSTANCE  " +
                            "SET ID_SERVICE = (SELECT ID_SERVICE " +
                            "FROM TASK T ,TOMODIFY TMOD WHERE TMOD.ID_TASK = T.ID_TASK(+) AND " +
                            "T.ID_TASK = ?) " +
                            "WHERE ID = ? ");
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, serviceInstanceId);
            preparedStatement.executeUpdate();
        } finally {
            try {
                close(connection, preparedStatement);
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
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT HAS_ACTIVE_TASK IS_BLOCKED FROM SERVICEINSTANCE WHERE ID = ? ");
            preparedStatement.setInt(1, serviceInstanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = (resultSet.getInt("IS_BLOCKED") == 1);
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }


    public boolean isInstanceDisconnected(int serviceInstanceId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try {
            String statusInst = InstanceStatus.DISCONNECTED.toString();
            preparedStatement = connection.
                    prepareStatement("SELECT SIS.NAME RES " +
                            "FROM SERVICEINSTANCE SI,SERVICEINSTANCESTATUS SIS " +
                            "WHERE SI.SERVICE_INSTANCE_STATUS = SIS.ID(+) " +
                            "AND SI.ID = ? ");
            preparedStatement.setInt(1, serviceInstanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String checkResult = null;
            if (resultSet.next()) {
                checkResult = resultSet.getString("RES");
            }
            if (checkResult.equals(statusInst)) {
                result = true;
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * return List<Services> of all Services
     *
     * @return List of Services
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public List<User> getUsersByUserRole(UserRole role) throws SQLException {
        ArrayList<User> result = new ArrayList<User>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String query = "SELECT * " +
                    "FROM USERS " +
                    "WHERE ID_USERROLE = (SELECT ID_USERROLE FROM USERROLE WHERE NAME = ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, role.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new User(resultSet.getInt("ID_USER"),
                        resultSet.getInt("ID_USERROLE"),
                        "CUSTOMER",
                        resultSet.getString("E_MAIL"),
                        resultSet.getString("F_NAME"),
                        resultSet.getString("L_NAME"),
                        resultSet.getString("PHONE"),
                        (resultSet.getInt("IS_BLOCKED") == 1)
                ));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        result.trimToSize();
        return result;

    }

    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getMostProfitableRouterForReport(int page, int pageLength) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        class MostProfRouter implements Serializable{
            private String router;
            private double profit;

            MostProfRouter(String router, double profit) {
                super();
                this.router = router;
                this.profit = profit;
            }

            public String getRouter() {
                return router;
            }

            public double getProfit() {
                return profit;
            }
        }
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT * FROM ( SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, " +
                            "SUM(S.PRICE) PROFIT, " +
                            "ROW_NUMBER() OVER (ORDER BY P.ID_ROUTER DESC) RN " +
                            "FROM CABLE C, PORT P, SERVICEINSTANCE SI, SERVICE S  " +
                            "WHERE C.ID_PORT = P.ID(+) AND  C.ID = SI.ID_CABLE(+) " +
                            "AND  SI.ID_SERVICE = S.ID(+) " +
                            "GROUP BY P.ID_ROUTER ORDER BY PROFIT DESC) " +
                            "WHERE RN BETWEEN ? AND ? ");
            preparedStatement.setInt(1, (page - 1) * pageLength + 1);
            preparedStatement.setInt(2, (page - 1) * pageLength + pageLength);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new MostProfRouter(resultSet.getString("ROUTER_NAME"), resultSet.getDouble("PROFIT")));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getUsedRoutersAndCapacityOfPorts(int page, int pageLength) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int amountOfPorts = CauliflowerInfo.PORTS_QUANTITY;
        class UsedRoutersAndCapacityOfPorts implements Serializable{
            private String router;
            private int occupied;
            private int free;
            private double utilization;

            public UsedRoutersAndCapacityOfPorts(String router, int occupied, int free, double utilization) {
                this.router = router;
                this.occupied = occupied;
                this.free = free;
                this.utilization = utilization;
            }

            public String getRouter() {
                return router;
            }

            public int getFree() {
                return free;
            }

            public int getOccupied() {
                return occupied;
            }

            public double getUtilization() {
                return utilization;
            }
        }
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT 'ROUTER-' ||ROUTEROLD ROUTER_NAME,  " +
                            "OCCUPIED, "+amountOfPorts+" - OCCUPIED FREE, " +
                            "ROUND( OCCUPIED / "+amountOfPorts+", 2) UTILIZATION " +
                            "FROM (  SELECT R.ID ROUTEROLD, SUM(p.Used) OCCUPIED, " +
                            "ROW_NUMBER() OVER (ORDER BY r.id ASC) RN  " +
                            "FROM ROUTER R ,PORT P " +
                            "WHERE R.ID = P.ID_ROUTER(+) " +
                            "GROUP BY R.ID " +
                            ")WHERE RN BETWEEN ? AND ? ");
            preparedStatement.setInt(1, (page - 1) * pageLength + 1);
            preparedStatement.setInt(2, (page - 1) * pageLength + pageLength);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new UsedRoutersAndCapacityOfPorts(resultSet.getString("ROUTER_NAME"),
                        resultSet.getInt("OCCUPIED"), resultSet.getInt("FREE"),
                        resultSet.getDouble("UTILIZATION")));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getProfitabilityByMonth(int page, int pageLength) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        class ProfitabilityByMonth implements Serializable{
            private String routerName;
            private double profit;

            ProfitabilityByMonth(String routerName, double profit) {
                this.routerName = routerName;
                this.profit = profit;
            }

            public String getRouterName() {
                return routerName;
            }

            public double getProfit() {
                return profit;
            }
        }
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT * FROM ( SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, " +
                            "SUM(S.PRICE) PROFIT, " +
                            "ROW_NUMBER() OVER (ORDER BY P.ID_ROUTER ASC) RN " +
                            "FROM CABLE C, PORT P, SERVICEINSTANCE SI, SERVICE S  " +
                            "WHERE C.ID_PORT = P.ID(+) AND  C.ID = SI.ID_CABLE(+) " +
                            "AND  SI.ID_SERVICE = S.ID(+) " +
                            "GROUP BY P.ID_ROUTER ) " +
                            "WHERE RN BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1) * pageLength + 1);
            preparedStatement.setInt(2, (page - 1) * pageLength + pageLength);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new ProfitabilityByMonth(resultSet.getString("ROUTER_NAME"), resultSet.getDouble("PROFIT")));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getCablesForReport(int page, int pageLength) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        class CablesForReport implements Serializable{
            private String cable;
            private String serviceInstanceAdress;

            public CablesForReport(String Cable, String serviceInstanceAdress) {
                super();
                this.cable = Cable;
                this.serviceInstanceAdress = serviceInstanceAdress;
            }

            public String getCable() {
                return cable;
            }

            public String getServiceInstanceAdress() {
                return serviceInstanceAdress;
            }
        }
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT * FROM ( " +
                            "SELECT 'CABLE-'||C.ID CABLE, L.ADRESS SERVICE_INSTANCE_ADRESS, " +
                            "ROW_NUMBER() OVER (ORDER BY C.ID ASC) RN " +
                            "FROM SERVICEINSTANCE SI, SERVICELOCATION SL,LOCATION L, CABLE C " +
                            "WHERE SI.ID_SERVICE_LOCATION = SL.ID(+) " +
                            "AND SL.ID_LOCATION = L.ID(+) " +
                            "AND SI.ID_CABLE = C.ID(+)) " +
                            "WHERE RN BETWEEN ? AND ? " +
                            "ORDER BY SERVICE_INSTANCE_ADRESS ASC ");
            preparedStatement.setInt(1, (page - 1) * pageLength + 1);
            preparedStatement.setInt(2, (page - 1) * pageLength + pageLength);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new CablesForReport(
                        resultSet.getString("CABLE"),
                        resultSet.getString("SERVICE_INSTANCE_ADRESS")));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * get amount of routers in system
     *
     * @return int amount of routers
     * @throws SQLException
     */
    private int getRoutersAmount() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT COUNT(*) AM " +
                            "FROM ROUTER ");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getInt("AM");

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * get amount of cables in system
     *
     * @return int amount of cables
     * @throws SQLException
     */
    private int getCablesAmount() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT COUNT(*) AM " +
                            "FROM  CABLE ");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getInt("AM");

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * get amount of ports in system
     *
     * @return int amount of ports
     * @throws SQLException
     */
    private int getPortsAmount() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT COUNT(*) AM FROM PORT ");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getInt("AM");

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * gets amount of lines in report for devices
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getDevicesReportLinesAmount() throws SQLException {
        int result = 0;
        result = getRoutersAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Circuits
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getCircuitsReportLinesAmount() throws SQLException {
        int result = 0;
        result = getCablesAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Cables
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getCablesReportLinesAmount() throws SQLException {
        int result = 0;
        result = getCablesAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Ports
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getPortsReportLinesAmount() throws SQLException {
        int result = 0;
        result = getPortsAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Most Profitable Router
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getMostProfitableRouterReportLinesAmount() throws SQLException {
        int result = 0;
        result = getRoutersAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Used Routers And Capacity Of Ports
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getUsedRoutersAndCapacityOfPortsLinesAmount() throws SQLException {
        int result = 0;
        result = getRoutersAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Profitability By Month
     *
     * @return int lines amount
     * @throws java.sql.SQLException
     */
    public int getProfitabilityByMonthLinesAmount() throws SQLException {
        int result = 0;
        result = getRoutersAmount();
        return result;
    }

    /**
     * gets amount of lines in report for Orders Per Period
     * @param scenario scenario "NEW" or "DISCONNECT" to special report
     * @param sqlStartDate start date
     * @param sqlEndDate end date
     * @return int amount of lines
     * @throws SQLException
     */
    public int getOrdersPerPeriodLinesAmount(Scenario scenario, java.sql.Date sqlStartDate, java.sql.Date sqlEndDate) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT COUNT(*) AM " +
                            "FROM SERVICEORDER SO,ORDERSCENARIO OS " +
                            "WHERE SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO(+) AND "+
                            "OS.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? " +
                            "GROUP BY OS.NAME ");
            preparedStatement.setString(1, scenario.toString());
            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlEndDate);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getInt("AM");
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return result;
    }
    public int getCIALinesAmount()throws SQLException{
        int result = 0;
        result = getCablesAmount();
        return result;
    }
    public List<Object> getCIAReport(int page, int pageLength) throws SQLException{
        class CIAReport implements Serializable{
            private String routerName;
            private String portName;
            private String serviceInstanceAdress;
            private String email;
            private String firstName;
            private String lastName;

            public CIAReport(String routerName, String portName, String serviceInstanceAdress,
                             String email, String firstName, String lastName) {
                this.routerName = routerName;
                this.portName = portName;
                this.serviceInstanceAdress = serviceInstanceAdress;
                this.email = email;
                this.firstName = firstName;
                this.lastName = lastName;
            }

            public String getRouterName() {
                return routerName;
            }

            public String getPortName() {
                return portName;
            }

            public String getServiceInstanceAdress() {
                return serviceInstanceAdress;
            }

            public String getEmail() {
                return email;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Object> result = new ArrayList<Object>();
        final String serviceInstanceStatus = InstanceStatus.ACTIVE.toString();
        final int startP = (page - 1) * pageLength + 1;
        final int endP = page * pageLength;
        final int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;
//        final String selectQuery = " SELECT * FROM ( " +
//                "SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, 'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, "+
//                portsQuantity+") PORT_NAME, L.ADRESS SERVICE_INSTANCE_ADRESS, " +
//                "U.E_MAIL USER_EMAIL, U.F_NAME USER_FIRST_NAME, U.L_NAME USER_LAST_NAME , " +
//                "ROW_NUMBER() OVER (ORDER BY L.ADRESS, P.ID_ROUTER, MOD(P.ID,"+portsQuantity+") ASC) RN " +
//                "FROM ((((( SERVICEINSTANCE SI INNER JOIN USERS U ON SI.ID_USER = U.ID_USER ) " +
//                "INNER JOIN CABLE C ON C.ID = SI.ID_CABLE )  " +
//                "INNER JOIN PORT P ON P.ID = C.ID_PORT )  " +
//                "INNER JOIN SERVICEINSTANCESTATUS SIST ON SIST.ID =  SI.SERVICE_INSTANCE_STATUS) " +
//                "inner join (SERVICELOCATION SL INNER JOIN LOCATION L " +
//                "ON SL.ID_LOCATION = L.ID) ON SL.ID = SI.ID_SERVICE_LOCATION) " +
//                "WHERE SIST.NAME = ? ) WHERE RN BETWEEN ? AND ? ";
        final String selectQuery = "SELECT * FROM ( " +
                "SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, " +
                "'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, "+portsQuantity+") PORT_NAME, " +
                "L.ADRESS SERVICE_INSTANCE_ADRESS, " +
                "U.E_MAIL USER_EMAIL, U.F_NAME USER_FIRST_NAME, " +
                "U.L_NAME USER_LAST_NAME , " +
                "ROW_NUMBER() OVER (ORDER BY L.ADRESS, P.ID_ROUTER, MOD(P.ID,"+portsQuantity+") ASC) RN " +
                "FROM SERVICEINSTANCE SI, SERVICELOCATION SL, LOCATION L, " +
                "USERS U, SERVICEINSTANCESTATUS SIST, CABLE C, PORT P " +
                "WHERE SI.ID_USER = U.ID_USER(+) AND SI.ID_SERVICE_LOCATION = SL.ID(+) " +
                "AND SL.ID_LOCATION = L.ID(+) AND SI.SERVICE_INSTANCE_STATUS = SIST.ID(+) " +
                "AND SI.ID_CABLE = C.ID(+) AND C.ID_PORT = P.ID(+) AND SIST.NAME = ? ) " +
                "WHERE RN BETWEEN ? AND ? ";
        try {
            preparedStatement = connection.
                    prepareStatement(selectQuery);
            preparedStatement.setString(1, serviceInstanceStatus);
            preparedStatement.setInt(2, startP);
            preparedStatement.setInt(3, endP);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new CIAReport(
                        resultSet.getString("ROUTER_NAME"),
                        resultSet.getString("PORT_NAME"),
                        resultSet.getString("SERVICE_INSTANCE_ADRESS"),
                        resultSet.getString("USER_EMAIL"),
                        resultSet.getString("USER_FIRST_NAME"),
                        resultSet.getString("USER_LAST_NAME")
                ));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement! in DAO.getCIAReport()");
                exc.printStackTrace();
            }
        }
        return result;
    }
    /**---------------------------------------------------------------------END KASPYAR---------------------------------------------------------------------**/


    /**
     * ---------------------------------------------------------------------IGOR---------------------------------------------------------------------*
     */


    /**
     * Create List<CIA> object to create report table for CIA Reports
     *
     * @return List<CIA>
     * @throws java.sql.SQLException
     */
    public List<CIA> getCIAReportForTable() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<CIA> result = new ArrayList<CIA>();
        final String sistActQ = InstanceStatus.ACTIVE.toString();

        // -- SELECT ROUTER ID, PORT ID, SI ID, USER ID, USER EMAIL, USER FNAME, USER LNAME
        final String selectQuery = "SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, 'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, 60) PORT_NAME, SI.ID SERVICE_INSTANCE_ID,  " +
                "U.E_MAIL USER_EMAIL, U.F_NAME USER_FIRST_NAME, U.L_NAME USER_LAST_NAME " +
                "FROM ((( SERVICEINSTANCE SI INNER JOIN USERS U ON SI.ID_USER = U.ID_USER )   " +
                "INNER JOIN CABLE C ON C.ID = SI.ID_CABLE )   " +
                "INNER JOIN PORT P ON P.ID = C.ID_PORT )  " +
                "INNER JOIN SERVICEINSTANCESTATUS SIST ON SIST.ID =  SI.SERVICE_INSTANCE_STATUS  " +
                "WHERE SIST.NAME = ? ";
        final String routerName = "ROUTER_NAME";
        final String portName = "PORT_NAME";
        final String serviceInstanceId = "SERVICE_INSTANCE_ID";
        final String userEmail = "USER_EMAIL";
        final String userFirstName = "USER_FIRST_NAME";
        final String userLastName = "USER_LAST_NAME";
        try {
            preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, sistActQ);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final CIA cia = new CIA(
                        resultSet.getString(routerName),
                        resultSet.getString(portName),
                        resultSet.getInt(serviceInstanceId),
                        resultSet.getString(userEmail),
                        resultSet.getString(userFirstName),
                        resultSet.getString(userLastName)
                );
                result.add(cia);
            }

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement! in DAO.getCIAReport()");
                exc.printStackTrace();
            }
        }
        {// help
            System.out.println("SUCCESS!!!! getCIAReport()");
        }
        return result;
    }


    /**
     * Create ReportGenerator object to generate report for CIA Reports
     *
     * @param EXT extension of file to generate report
     * @return ReportGenerator for
     * @throws java.sql.SQLException
     */
    public ReportGenerator getCIAReport(final String EXT) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        final String xlsExt = "xls";
        int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;

        // -- SELECT ROUTER ID, PORT ID, SI ID, USER ID, USER EMAIL, USER FNAME, USER LNAME
        final String selectQuery = "SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, 'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, "+portsQuantity+") PORT_NAME, L.ADRESS SERVICE_INSTANCE_ADRESS, " +
                "U.E_MAIL USER_EMAIL, U.F_NAME USER_FIRST_NAME, U.L_NAME USER_LAST_NAME " +
                "FROM ((((( SERVICEINSTANCE SI INNER JOIN USERS U ON SI.ID_USER = U.ID_USER ) " +
                "INNER JOIN CABLE C ON C.ID = SI.ID_CABLE )  " +
                "INNER JOIN PORT P ON P.ID = C.ID_PORT )  " +
                "INNER JOIN SERVICEINSTANCESTATUS SIST ON SIST.ID =  SI.SERVICE_INSTANCE_STATUS) " +
                "inner join (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) ON SL.ID = SI.ID_SERVICE_LOCATION)" +
                "WHERE SIST.NAME = ? ORDER BY SERVICE_INSTANCE_ADRESS, P.ID_ROUTER, MOD(P.ID,"+portsQuantity+") ASC";

        final String sistActQ = InstanceStatus.ACTIVE.toString();
        try {
            preparedStatement = connection
                    .prepareStatement(selectQuery);
            preparedStatement.setString(1, sistActQ);
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals(xlsExt)) {
                reportGenerator = new XLSReportGenerator(" CIA Report ",
                        resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {// help
            System.out.println("SUCCESS!!!!getCIAReport");
        }
        return reportGenerator;
    }

    /**
     * Prepare ResultSet  to generate report on used routers and port capacity
     *
     * @return ResultSet for sql request
     * @throws java.sql.SQLException
     */
    public ReportGenerator getUsedRoutersAndCapacityOfPorts(final String EXT) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        int amountOfPorts = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT 'ROUTER-' ||ROUTEROLD ROUTER,  " +
                            "OCCUPIED, "+amountOfPorts+" - OCCUPIED FREE, ROUND( OCCUPIED / "+amountOfPorts+", 2) UTILIZATION " +
                            "FROM ( SELECT R.ID ROUTEROLD, SUM(p.Used) OCCUPIED " +
                            "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                            "GROUP BY R.ID ORDER BY R.ID )");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Routers and capacity of ports", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getUsedRoutersAndCapacityOfPorts");
        }
        return reportGenerator;
    }

    /**
     * Prepare ResultSet  to generate report on the most profitable router
     *
     * @return ResultSet for sql request
     * @throws java.sql.SQLException
     */

    public ReportGenerator getProfitabilityByMonth(final String EXT) throws SQLException {
        {//help
            System.out.println("getProfitabilityByMonth");
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReportGenerator reportGenerator = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT 'ROUTER-'||P.ID_ROUTER ROUTER_NAME, SUM(S.PRICE) PROFIT " +
                    "FROM SERVICE S INNER JOIN ( " +
                    "  SERVICEINSTANCE SI INNER JOIN ( " +
                    "    CABLE C INNER JOIN PORT P ON C.ID_PORT = P.ID)  " +
                    "  ON SI.ID_CABLE = C.ID) " +
                    "ON  S.ID = SI.ID_SERVICE " +
                    "GROUP BY P.ID_ROUTER ORDER BY P.ID_ROUTER ASC");
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Profitability by month", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
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

    public ReportGenerator getOrdersPerPeriod(Scenario scenario, java.sql.Date sqlStartDate, java.sql.Date sqlEndDate, final String EXT) throws SQLException {

        {//help
            System.out.println("getOrdersPerPeriod");
        }
        Connection connection = getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ReportGenerator reportGenerator = null;
        try {
//            preparedStatement = connection.prepareStatement("SELECT OS.NAME SCENARIO, COUNT(*) AMOUNT " +
//                    "FROM SERVICEORDER SO INNER JOIN ORDERSCENARIO OS ON SO.ID_ORDERSCENARIO = OS.ID_ORDERSCENARIO " +
//                    "WHERE OS.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? " +
//                    "GROUP BY OS.NAME ");
//            preparedStatement = connection.prepareStatement("SELECT * FROM(SELECT  st.NAME, st.SPEED, OST.NAME STATUS_NAME, SO.OUR_DATE SO_DATE,  " +
//                    "U.F_NAME, U.L_NAME " +
//                    "FROM  " +
//                    "((( SERVICEORDER SO INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS)  " +
//                    "INNER JOIN SERVICEINSTANCE SI ON SI.ID = SO.ID_SRVICEINSTANCE) " +
//                    "INNER JOIN ORDERSCENARIO  OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
//                    "INNER JOIN USERS U ON U.ID_USER = SI.ID_USER inner join (service s inner join servicetype st on s.ID_SERVICE_TYPE = st.ID)on SI.ID_SERVICE = s.ID " +
//                    "WHERE OSC.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? ) ");
            preparedStatement = connection.prepareStatement("SELECT  SO.OUR_DATE SERVICE_ORDER_DATE, U.F_NAME FIRST_NAME, U.L_NAME LAST_NAME, st.NAME SERVICE_NAME, st.SPEED, OST.NAME STATUS_NAME   " +
                    "FROM  " +
                    "((( SERVICEORDER SO INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS) " +
                    "INNER JOIN SERVICEINSTANCE SI ON SI.ID = SO.ID_SRVICEINSTANCE) " +
                    "INNER JOIN ORDERSCENARIO  OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
                    "INNER JOIN USERS U ON U.ID_USER = SI.ID_USER inner join (service s inner join servicetype st on s.ID_SERVICE_TYPE = st.ID)on SI.ID_SERVICE = s.ID " +
                    "WHERE OSC.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? ORDER BY SO.OUR_DATE ASC ");

            preparedStatement.setString(1, scenario.toString());
            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlEndDate);
            resultSet = preparedStatement.executeQuery();
            if (EXT.equals("xls")) {
                reportGenerator = new XLSReportGenerator("Get " + scenario + " orders", resultSet);
            } else {
                reportGenerator = new CSVReportGenerator(resultSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatement);
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
        final String selectQuery =
                "SELECT SI.ID SI_ID, C.ID C_ID , C.ID_PORT C_ID_PORT "
                        + " FROM (SERVICEORDER SO INNER JOIN SERVICEINSTANCE SI ON SI.ID = SO.ID_SRVICEINSTANCE) "
                        + " INNER JOIN CABLE C ON SI.ID_CABLE = C.ID "
                        + " WHERE SO.ID_SERVICEORDER = ? ";
        final String siIdQ = "SI_ID";
        final String cIdQ = "C_ID";
        final String pIdQ = "C_ID_PORT";
        final String updatePortQ = " UPDATE PORT SET USED = 0 WHERE ID = ? ";
        final String updateSIQ = " UPDATE SERVICEINSTANCE SET ID_CABLE = NULL WHERE ID = ? ";
        final String deleteCableQ = " DELETE FROM CABLE WHERE ID = ? ";


        try {
            connection.setAutoCommit(false);
            // ---- GET SI ID, CABLE ID, PORT ID BY SO ID
            preparedStatementSelect = connection
                    .prepareStatement(selectQuery);
            preparedStatementSelect.setInt(1, serviceOrderId);

            resultSet = preparedStatementSelect.executeQuery();
            while (resultSet.next()) {
                siID = resultSet.getInt(siIdQ);
                cableID = resultSet.getInt(cIdQ);
                portID = resultSet.getInt(pIdQ);
            }

            if (siID != checkNumber && cableID != checkNumber
                    && portID != checkNumber) {

                // ---- UPDATE PORT USED SET 0
                preparedStatementUpdate = connection
                        .prepareStatement(updatePortQ);
                preparedStatementUpdate.setInt(1, portID);
                preparedStatementUpdate.executeUpdate();

                // ---- UPDATE SERVICEINSTANCE ID_CABLE SET NULL
                preparedStatementUpdate = connection
                        .prepareStatement(updateSIQ);
                preparedStatementUpdate.setInt(1, siID);
                preparedStatementUpdate.executeUpdate();

                // ---- DELETE FROM CABLE WHERE ID = cableID
                preparedStatementUpdate = connection
                        .prepareStatement(deleteCableQ);
                preparedStatementUpdate.setInt(1, cableID);
                preparedStatementUpdate.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connection, preparedStatementSelect);
                close(connection, preparedStatementUpdate);
            } catch (SQLException e) {
                logger.info("Smth wrong with closing connection or preparedStatement! in DAO.removeCableFromServiceInstanceAndFreePort ");
                e.printStackTrace();
            }

        }
    }

    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getDevicesForReport(int page, int pageLength) throws SQLException {
        class Device implements Serializable {

            private String deviceName;
            private int occupied;
            private int free;

            public Device(String deviceName, int occupied, int free) {
                this.deviceName = deviceName;
                this.occupied = occupied;
                this.free = free;
            }

            public String getDeviceName() {
                return deviceName;
            }

            public int getOccupied() {
                return occupied;
            }

            public int getFree() {
                return free;
            }
        }
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        final int maxPortsCapOnDevice = CauliflowerInfo.PORTS_QUANTITY;
        List<Object> devices = new ArrayList<Object>();
        final int startP = (page - 1) * pageLength + 1;
        final int endP = page * pageLength;
        try {
            preparedStatement = connection.
                    prepareStatement("SELECT 'ROUTER-'||R2.ID ROUTER_NAME, R2.USED OCCUPIED " +
                            "FROM (SELECT R.ID, SUM(P.USED) USED, ROW_NUMBER() OVER (ORDER BY R.ID) RN " +
                            "FROM (ROUTER R INNER JOIN PORT P ON R.ID = P.ID_ROUTER) " +
                            "GROUP BY R.ID) R2 " +
                            "WHERE RN BETWEEN ? AND ? ");

            preparedStatement.setInt(1, startP);
            preparedStatement.setInt(2, endP);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                devices.add(
                        new Device(
                                resultSet.getString("ROUTER_NAME"),
                                resultSet.getInt("OCCUPIED"),
                                (maxPortsCapOnDevice - resultSet.getInt("OCCUPIED"))));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException e) {
                logger.warn("Smth wrong with closing connection or preparedStatement!");
                e.printStackTrace();
            }
        }
        return devices;
    }


    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getCircuitsForReport(int page, int pageLength) throws SQLException {
        Connection connection = getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Object> circuits = new ArrayList<Object>();
        final int startP = (page - 1) * pageLength + 1;
        final int endP = page * pageLength;
        class Circuit implements Serializable {
            private String router;
            private String port;
            private String cable;
            private String serviceInstanceAdress;

            public Circuit(String router, String port, String cable, String serviceInstanceAdress) {
                this.router = router;
                this.port = port;
                this.cable = cable;
                this.serviceInstanceAdress = serviceInstanceAdress;
            }

            public String getCableId() {
                return cable;
            }
            public String getPortId() {
                return port;
            }
            public String getRouterId() {
                return router;
            }
            public String getServiceInstanceAdress() {
                return serviceInstanceAdress;
            }
        }
        int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.
                    prepareStatement(
                            "SELECT * FROM (SELECT 'CABLE-'||C.ID CABLE, " +
                                    "'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, "+portsQuantity+") PORT, " +
                                    "'ROUTER-'||P.ID_ROUTER ROUTER, L.ADRESS SERVICE_INSTANCE_ADRESS , " +
                                    "ROW_NUMBER() OVER (ORDER BY P.ID_ROUTER, MOD(P.ID, "+portsQuantity+") ASC) RN " +
                                    "FROM ((ROUTER r INNER JOIN PORT p ON r.Id = P.Id_Router) " +
                                    "INNER JOIN CABLE c ON p.Id = C.Id_Port) INNER JOIN (SERVICEINSTANCE si " +
                                    "inner join (SERVICELOCATION SL INNER JOIN LOCATION L ON SL.ID_LOCATION = L.ID) " +
                                    "ON SI.ID_SERVICE_LOCATION = SL.ID ) " +
                                    "ON C.Id = Si.Id_Cable )" +
                                    "WHERE RN BETWEEN ? AND ? ");
            preparedStatement
                    .setInt(1, startP);
            preparedStatement
                    .setInt(2, endP);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                circuits.add(new Circuit(resultSet.getString("CABLE"),
                        resultSet.getString("PORT"),
                        resultSet.getString("ROUTER"), resultSet.getString("SERVICE_INSTANCE_ADRESS")));
            }

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement! in DAO.getCircuitsForReport(int page, int pageLength)");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getCircuitsForReport");
        }
        return circuits;
    }


    /**
     * Get certain sum of lines for report
     *
     * @param pageLength amount of lines per page
     * @param page       number of page
     * @throws java.sql.SQLException
     */
    public List<Object> getPortsForReport(int page, int pageLength) throws SQLException {
        System.out.println("getPortsForReport");
        Connection connection = getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Object> ports = new ArrayList<Object>();
        final int startP = (page - 1) * pageLength + 1;
        final int endP = page * pageLength;
        class Port implements Serializable{
            private String router;
            private String port;
            private String used;

            public Port(String router,String port,  String used) {
                this.port = port;
                this.router = router;
                this.used = used;
            }

            public String getRouter() {
                return router;
            }

            public String getPort() {
                return port;
            }

            public String getUsed() {
                return used;
            }
        }
        int portsQuantity = CauliflowerInfo.PORTS_QUANTITY;
        try {
            preparedStatement = connection.prepareStatement
                    ("SELECT * FROM(  " +
                            "SELECT 'ROUTER-'||P.ID_ROUTER ROUTER, " +
                            "'ROUTER-'||P.ID_ROUTER||'-'||MOD(P.ID, "+portsQuantity+") PORT,  " +
                            "CASE P.USED WHEN 1 THEN 'YES' ELSE 'NO' END USED," +
                            "ROW_NUMBER() OVER (ORDER BY P.ID_ROUTER, MOD(P.ID,"+portsQuantity+") ASC) RN " +
                            "FROM PORT P )  " +
                            "WHERE RN BETWEEN ? AND ?");
            preparedStatement
                    .setInt(1, startP);
            preparedStatement
                    .setInt(2, endP);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Port p = new Port(resultSet.getString("ROUTER"),
                        resultSet.getString("PORT"),
                        resultSet.getString("USED"));
                ports.add(p);

            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement! in DAO.getPortsForReport(int page, int pageLength)");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getPortsForReport");
        }
        return ports;
    }


    public List<Object> getOrdersPerPeriod(Scenario scenario, java.sql.Date sqlStartDate, java.sql.Date sqlEndDate, final int page, final int pageLength) throws SQLException {
        Connection connection = getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Object> servOrds = new ArrayList<Object>();
        class Orders implements  Serializable{
            private String nameService;
            private double speed;
            private String statusName;
            private java.sql.Date serviceOrderDate;
            private String firstName;
            private String lastName;

            public Orders(String nameService, double speed, String statusName, java.sql.Date date,
                          String firstName, String lastName) {
                this.nameService = nameService;
                this.speed = speed;
                this.statusName = statusName;
                this.serviceOrderDate = date;
                this.firstName = firstName;
                this.lastName = lastName;
            }

            public String getNameService() {
                return nameService;
            }

            public double getSpeed() {
                return speed;
            }

            public String getStatusName() {
                return statusName;
            }

            public java.sql.Date getServiceOrderDate() {
                return serviceOrderDate;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }
        }
        final int startP = (page - 1) * pageLength + 1;
        final int endP = page * pageLength;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM " +
                    "(SELECT  st.NAME, st.SPEED, OST.NAME STATUS_NAME, SO.OUR_DATE SERVICE_ORDER_DATE,  " +
                    "U.F_NAME, U.L_NAME, ROW_NUMBER() OVER (ORDER BY SO.OUR_DATE ASC) RN  " +
                    "FROM  ((( SERVICEORDER SO INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS) " +
                    "INNER JOIN SERVICEINSTANCE SI ON SI.ID = SO.ID_SRVICEINSTANCE) " +
                    "INNER JOIN ORDERSCENARIO  OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
                    "INNER JOIN USERS U ON U.ID_USER = SI.ID_USER inner join (service s inner join servicetype st on s.ID_SERVICE_TYPE = st.ID)on SI.ID_SERVICE = s.ID " +
                    "WHERE OSC.NAME = ? AND SO.OUR_DATE BETWEEN ? AND ? ) " +
                    "WHERE RN BETWEEN ? AND ? ");
            System.out.println("Before set");
            preparedStatement.setString(1, scenario.toString());
            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlEndDate);
            preparedStatement.setInt(4, startP);
            preparedStatement.setInt(5, endP);
            System.out.println(preparedStatement.toString());
            resultSet = preparedStatement.executeQuery();
            System.out.println("Executed!!!");
            while (resultSet.next()) {
                final Orders so = new Orders(
                        resultSet.getString("NAME"), resultSet.getDouble("SPEED"),
                        resultSet.getString("STATUS_NAME"), resultSet.getDate("SERVICE_ORDER_DATE"),
                        resultSet.getString("F_NAME"), resultSet.getString("L_NAME")
                );
                servOrds.add(so);
            }

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        {//help
            System.out.println("SUCCESS!!!!getOrdersPerPeriod");
        }
        return servOrds;
    }


    /**---------------------------------------------------------------------END IGOR---------------------------------------------------------------------**/

    /**---------------------------------------------------------------------START vladmyr---------------------------------------------------------------------**/
    /**
     * return User of customer user
     *
     * @return User
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.Service
     */
    public User getCustomerUserById(int id) throws SQLException {
        final int CUSTOMER = 1;
        {//
            System.out.println("getCustomerUserById");
        }
        User result = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String query = "SELECT * FROM USERS WHERE ID_USERROLE = ? AND ID_USER = ?";
            {//
                System.out.println(query);
            }
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, CUSTOMER);
            preparedStatement.setInt(2, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result = new User(
                        resultSet.getInt("ID_USER"),
                        resultSet.getInt("ID_USERROLE"),
                        "CUSTOMER",
                        resultSet.getString("E_MAIL"),
                        resultSet.getString("F_NAME"),
                        resultSet.getString("L_NAME"),
                        resultSet.getString("PHONE"),
                        ((resultSet.getString("IS_BLOCKED") == "1") ? true : false));
            }
            {//
                System.out.println("SUCCESS!!! getCustomerUserById");
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;

    }

    /**
     * return ServiceInstance of service instances
     *
     * @return ServiceInstance
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceInstance
     */
    public ServiceInstance getInstanceById(int serviceInstanceId) throws SQLException {
        {//help
            System.out.println("GET INSTANCE BY ID");
        }
        ServiceInstance result = null;
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
                    "WHERE SI.ID = ?");
            preparedStatement.setInt(1, serviceInstanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result = new ServiceInstance(
                        resultSet.getInt("ID"),
                        resultSet.getInt("ID_USER"),
                        resultSet.getInt("ID_SERVICE_LOCATION"),
                        resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE"),
                        resultSet.getInt("ID_SERVICE"),
                        resultSet.getInt("SERVICE_INSTANCE_STATUS"),
                        resultSet.getString("NAME"),
                        resultSet.getInt("ID_CABLE"),
                        (resultSet.getInt("HAS_ACTIVE_TASK") == 1));
            }
            {//help
                System.out.println("SUCCESS !!! GET INSTANCES");
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;

    }

    /**
     * Returns ServiceOrder for selected task
     *
     * @param serviceOrderId Id of the service order
     * @return found ServiceOrder
     * @throws java.sql.SQLException
     * @see com.naukma.cauliflower.entities.ServiceOrder
     */

    public ServiceOrder getServiceOrderById(int serviceOrderId) throws SQLException {
        {//help
            System.out.println("getServiceOrderById");
        }
        ServiceOrder result = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SO.ID_SERVICEORDER, SO.ID_ORDERSTATUS, OST.NAME OST_NAME, " +
                    "SO.ID_SRVICEINSTANCE, SO.ID_ORDERSCENARIO, OSC.NAME OSC_NAME, SO.OUR_DATE, SO.ID_USER " +
                    "FROM (((TASK T INNER JOIN SERVICEORDER SO ON T.ID_SERVICEORDER = SO.ID_SERVICEORDER) " +
                    "INNER JOIN ORDERSTATUS OST ON SO.ID_ORDERSTATUS = OST.ID_ORDERSTATUS " +
                    ") INNER JOIN ORDERSCENARIO OSC ON SO.ID_ORDERSCENARIO = OSC.ID_ORDERSCENARIO) " +
                    "WHERE SO.ID_SERVICEORDER = ?");
            preparedStatement.setInt(1, serviceOrderId);
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
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**
     * gets service to modify instance for scenario MODIFY
     *
     * @param taskId selected task
     * @throws java.sql.SQLException
     */
    public Service getServiceModifyToByTaskId(int taskId) throws SQLException {
        {//help
            System.out.println("getServiceModifyToByTaskId");
        }
        Service result = null;
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT S.ID_SERVICE_TYPE, L.ADRESS, L.LONGITUDE, L.LATITUDE, " +
                    "ST.NAME, ST.SPEED, S.ID_PROVIDER_LOCATION, S.ID, S.PRICE " +
                    "FROM (SERVICE S INNER JOIN SERVICETYPE ST ON S.ID_SERVICE_TYPE = ST.ID) " +
                    "INNER JOIN LOCATION L ON S.ID_PROVIDER_LOCATION = L.ID " +
                    "WHERE S.ID = (SELECT ID_SERVICE FROM TOMODIFY WHERE ID_TASK = ?) ");
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = new Service(resultSet.getInt("ID_SERVICE_TYPE"),
                        resultSet.getString("ADRESS"),
                        resultSet.getDouble("LONGITUDE"),
                        resultSet.getDouble("LATITUDE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("SPEED"),
                        resultSet.getInt("ID_PROVIDER_LOCATION"),
                        resultSet.getInt("ID"),
                        resultSet.getDouble("PRICE"));
            }
            {//help
                System.out.println("SUCCESS ! getServiceModifyToByTaskId");
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return result;
    }

    /**---------------------------------------------------------------------END vladmyr---------------------------------------------------------------------**/

    /**
     * ---------------------------------------------------------------------START Alex---------------------------------------------------------------------*
     */
    public int countNotCompletedTasksByTaskName(TaskName taskName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int taskCount = 0;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(ID_TASK) RES FROM TASK WHERE NAME = ? AND ID_TASKSTATUS IN " +
                    "(SELECT ID_TASKSTATUS FROM TASKSTATUS WHERE NAME != ? )");
            preparedStatement.setString(1, taskName.toString());
            preparedStatement.setString(2, TaskStatus.COMPLETED.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                taskCount = resultSet.getInt("RES");
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return taskCount;
    }


    /**---------------------------------------------------------------------END Alex---------------------------------------------------------------------**/


    /**
     * ---------------------------------------------------------------------START Max---------------------------------------------------------------------*
     */

    public int getTasksNumByName(TaskName taskName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int num = 1;
        try {

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return num;
    }

    public int getFreePortsNum() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int num = 1;
        try {

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return num;
    }

    public int getTasksNumByStatus(TaskStatus taskStatus) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        int num = 1;
        try {

        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return num;

    }
    /**---------------------------------------------------------------------END Max---------------------------------------------------------------------**/

    /**---------------------------------------------------------------------START ihor---------------------------------------------------------------------**/

    /**
     * gets emails that include of the string that is the parameter queryString
     *
     * @param queryString string that is possibly included to email
     * @return ArrayList<String> consists of emais that is consisted of the parameter queryString
     * @throws java.sql.SQLException
     */
    public ArrayList<String> getEmailsLike(String queryString) throws SQLException {
        final String columLabel = "E_MAIL";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        final String query = "SELECT * FROM USERS WHERE E_MAIL like ? ";
        ArrayList<String> resEmails = new ArrayList<String>();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, queryString);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                resEmails.add(resultSet.getString(columLabel));
            }
        } finally {
            try {
                close(connection, preparedStatement);
            } catch (SQLException exc) {
                logger.warn("Can't close connection or preparedStatement!");
                exc.printStackTrace();
            }
        }
        return resEmails;
    }

/**---------------------------------------------------------------------END ihor---------------------------------------------------------------------**/
}


