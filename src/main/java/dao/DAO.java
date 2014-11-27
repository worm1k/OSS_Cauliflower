package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Eugene on 26.11.2014.
 */
public class DAO {
    private final String base_server = "jdbc:oracle:thin:@localhost:1521:XE";
    private final String base_user = "cauliflower";
    private final String base_pass = "11111";
    private Connection connection;
    // assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(DAO.class.getName());

    public DAO(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(base_server, base_user, base_pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getInfroFromENP(){
        String result = "";
        Statement st = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM USERS";
            st = connection.createStatement();
            st.execute(query);
            resultSet =  st.getResultSet();
            while (resultSet.next()){
                result += resultSet.getNString(1)  +  resultSet.getNString(2)   ;
            }
        } catch (SQLException e) {
           // e.printStackTrace();
            result +=e.getMessage();
        }
        return  result;
    }

    public Connection getConnection(){
        return  connection;
    }
}
