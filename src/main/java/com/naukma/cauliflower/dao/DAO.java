package com.naukma.cauliflower.dao;

import com.naukma.cauliflower.entities.User;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Slavko_O on 28.11.2014.
 */
public enum DAO {
    INSTANCE;
    private DataSource dataSource;
    private PreparedStatement preparedStatement;
    // assumes the current class is called logger
    private final Logger logger = Logger.getLogger(DAO.class.getName());
    private DAO(){
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
    public User getUserByLoginAndPassword(String login, String password)throws NullPointerException{
        Connection connection = getConnection();
        User user = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * " +
                    "FROM USERS U INNER JOIN USERROLE UR ON U.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? AND PASSWORD = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int idUser = resultSet.getInt("ID_USER");
                int idUserrole = resultSet.getInt("ID_USERROLE");
                String eMail = resultSet.getString("E_MAIL");
                String firstName = resultSet.getString("F_NAME");
                String lastName = resultSet.getString("L_NAME");
                String phone = resultSet.getString("PHONE");
                String userrole = resultSet.getString("NAME");
                user = new User(idUser, idUserrole,userrole,  eMail, firstName, lastName, phone);
            }
            resultSet.close();
         } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                if (!preparedStatement.isClosed()) preparedStatement.close();
                if (!connection.isClosed())connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return user;
    }

    public ResultSet reportTester() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM USERS");
        ResultSet rs = preparedStatement.executeQuery();
        return  rs;
    }
    }


