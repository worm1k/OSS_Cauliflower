package com.naukma.cauliflower.dao;

import com.naukma.cauliflower.entities.User;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DAO {
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement statement;

    private DAO() throws Exception {
        try {
            InitialContext ctx = new InitialContext();
            this.dataSource = (DataSource) ctx.lookup("jdbc/oraclesource");
        } catch (Exception e) {
            throw e;
        }

    }

    public static DAO getInstance() {
        return DAOHolder.INSTANCE;
    }

    public void open() throws SQLException {
        try {
            if (this.connection == null || this.connection.isClosed())
                this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed())
                this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByLoginAndPassword(String login, String password) throws NullPointerException {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        User user = null;
        try {
            statement = connection.prepareStatement("SELECT * " +
                    "FROM USERS U INNER JOIN USERROLE UR ON U.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? AND PASSWORD = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int ID_USER = resultSet.getInt(1);
                int ID_USERROLE = resultSet.getInt(2);
                String E_MAIL = resultSet.getString(3);
                String F_NAME = resultSet.getString(5);
                String L_NAME = resultSet.getString(6);
                String PHONE = resultSet.getString(7);
                String USERROLE = resultSet.getString("NAME");
                user = new User(ID_USER, ID_USERROLE, USERROLE, E_MAIL, F_NAME, L_NAME, PHONE);
            }

            if (user == null) throw new NullPointerException("No such user");
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return user;
    }


    public ResultSet reportTester() throws SQLException {
        open();
        statement = connection.prepareStatement("SELECT * FROM USERS");
        ResultSet rs = statement.executeQuery();
        close();
        return  rs;

    }

    private static class DAOHolder {

        public static final DAO INSTANCE;

        static {
            DAO dm;
            try {
                dm = new DAO();
            } catch (Exception e) {
                dm = null;
            }
            INSTANCE = dm;


        }
    }


}
/*
public class DAO {
    private static DataSource ds;
    private PreparedStatement statement;
    private User user = null;
    // assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(DAO.class.getName());

    public DAO(){
        if (ds != null) return;
        try {
            InitialContext ic = new InitialContext();
            ds = (DataSource) ic.lookup("jdbc/oraclesource");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){

        if (ds == null) new DAO();
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }
    public User getUserByLoginAndPassword(String login, String password)throws NullPointerException{
        try {
            statement = getConnection().prepareStatement("SELECT * " +
                    "FROM USERS U INNER JOIN USERROLE UR ON U.ID_USERROLE = UR.ID_USERROLE " +
                    "WHERE E_MAIL = ? AND PASSWORD = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int ID_USER = resultSet.getInt(1);
                int ID_USERROLE = resultSet.getInt(2);
                String E_MAIL = resultSet.getString(3);
                String F_NAME = resultSet.getString(5);
                String L_NAME = resultSet.getString(6);
                String PHONE = resultSet.getString(7);
                String USERROLE = resultSet.getString("NAME");
                user = new User(ID_USER, ID_USERROLE,USERROLE,  E_MAIL, F_NAME, L_NAME, PHONE);
            }
            if(user == null) throw new NullPointerException("No such user");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return user;
    }
    public User getUser(){
        return user;
    }

    public ResultSet reportTester() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM USERS");
        ResultSet rs = preparedStatement.executeQuery();
        return  rs;
    }
}*/
