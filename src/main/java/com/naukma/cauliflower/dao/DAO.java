package com.naukma.cauliflower.dao;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Eugene on 26.11.2014.
 */
public class DAO {
    private static DataSource ds;
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
}
