package com.naukma.cauliflower.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Eugene on 07.12.2014.
 */
public class PreparedStatementBlocker{
    private PreparedStatement value;
    private boolean blocked;
    private static Connection conn;
    private String query;
    private boolean needToclose;

    /**
     * Get user by its login and password
     *
     * @param conn    key for returned PreparedStatement object in hashMapForPreparedStatement, if there's such
     * @param query query for returned PreparedStatement object in hashMapForPreparedStatement
     * @return PreparedStatement for query
     */
    public PreparedStatementBlocker(String query, Connection conn) throws SQLException {
        this.blocked = false;
        this.conn = conn;
        this.query = query;
        this.value = conn.prepareStatement(query);
        needToclose = false;
    }

    public PreparedStatementBlocker(String query, Connection conn, boolean needToclose) throws SQLException {
        this.blocked = false;
        this.conn = conn;
        this.query = query;
        this.value = conn.prepareStatement(query);
        this.needToclose = needToclose;
    }

    public void setInt(int pos, int value) throws SQLException {
        this.value.setInt(pos, value);
    }

    public void setString(int pos, String value) throws SQLException {
        this.value.setString(pos, value);
    }

    public ResultSet executeQuery() throws SQLException {
        {//help
            System.out.println("Executed");
        }

        return value.executeQuery();
    }

    public void executeUpdate() throws SQLException {
        value.executeUpdate();
        {//help
            System.out.println("Executed");
        }
    }


    public boolean needToclose() {
        return needToclose;
    }

    public PreparedStatementBlocker block() {
        blocked = true;
        return this;
    }

    public boolean blocked() {
        return blocked;
    }

    public void close() throws SQLException {
        if(needToclose) {
            conn.close();
            value.close();
        }else{

            {//help
                System.out.println("Unblocked");
            }
            blocked = false;
        }
    }
}
