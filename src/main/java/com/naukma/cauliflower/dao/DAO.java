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
    private DataSource ds;
    private PreparedStatement pSt;
    // assumes the current class is called logger
    private final Logger LOGGER = Logger.getLogger(DAO.class.getName());
    private DAO(){
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("jdbc/oraclesource");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    private Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public User getUserByLoginAndPassword(String login, String password)throws NullPointerException{
        Connection connection = getConnection();
        User user = null;
        try {
            pSt = connection.prepareStatement("SELECT * " +
                    "FROM USERS U INNER JOIN USERROLE UR ON U.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? AND PASSWORD = ?");
            pSt.setString(1, login);
            pSt.setString(2, password);
            ResultSet resultSet = pSt.executeQuery();
            if (resultSet.next()){
                int ID_USER = resultSet.getInt(1);
                int ID_USERROLE = resultSet.getInt(2);
                String E_MAIL = resultSet.getString(3);
                String F_NAME = resultSet.getString(5);
                String L_NAME = resultSet.getString(6);
                String PHONE = resultSet.getString(7);
                String USERROLE = resultSet.getString("NAME");
                user = new User(ID_USER, ID_USERROLE,USERROLE,  E_MAIL, F_NAME, L_NAME, PHONE);
            }
            resultSet.close();
            if(user == null) throw new NullPointerException("No such user");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        finally{
            try {
                if (!pSt.isClosed())pSt.close();
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


