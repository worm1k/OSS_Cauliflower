package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import entities.User;

/**
 * Created by Eugene on 26.11.2014.
 */
public class UserDAO extends DAO{
    private PreparedStatement findUser;
    private User user = null;

    public UserDAO(String login, String password)throws NullPointerException{
        super();
        try {
            findUser = getConnection().prepareStatement("SELECT * FROM USERS WHERE E_MAIL = ? AND PASSWORD = ?");
            findUser.setString(1 , login);
            findUser.setString(2, password);
            ResultSet resultSet = findUser.executeQuery();
            while (resultSet.next()){
                int ID_USER = resultSet.getInt(1);
                int ID_USERROLE = resultSet.getInt(2);
                String E_MAIL = resultSet.getString(3);
                String PASSWORD = resultSet.getString(4);
                String F_NAME = resultSet.getString(5);
                String L_NAME = resultSet.getString(6);
                String PHONE = resultSet.getString(7);
                user = new User(ID_USER, ID_USERROLE, E_MAIL, PASSWORD, F_NAME, L_NAME, PHONE);
            }
            if(user == null) throw new NullPointerException("No such user");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
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
}
