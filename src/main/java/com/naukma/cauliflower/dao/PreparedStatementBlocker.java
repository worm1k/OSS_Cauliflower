package com.naukma.cauliflower.dao;

import java.sql.*;

/**
 * Created by Eugene on 07.12.2014.
 */
public class PreparedStatementBlocker{
    private PreparedStatement value;
    private volatile boolean  blocked;
    private static Connection conn;
    private String query;
    private boolean needToclose;
    private long timeOfLife;

    /**
     * Get user by its login and password
     *
     * @param conn    key for returned PreparedStatement object in hashMapForPreparedStatement, if there's such
     * @param query query for returned PreparedStatement object in hashMapForPreparedStatement
     * @return PreparedStatement for query
     */
    public PreparedStatementBlocker(String query, Connection conn) throws SQLException {
        timeOfLife = System.currentTimeMillis();
        this.blocked = false;
        this.conn = conn;
        this.query = query;
        this.value = conn.prepareStatement(query);
        needToclose = false;
    }

    /**
     * Get user by its login and password
     *
     * @param conn    key for returned PreparedStatement object in hashMapForPreparedStatement, if there's such
     * @param query query for returned PreparedStatement object in hashMapForPreparedStatement
     * @param needToclose if new object created and needed to be closed than
     * @return PreparedStatement for query
     */
    public PreparedStatementBlocker(String query, Connection conn, boolean needToclose) throws SQLException {
        timeOfLife = System.currentTimeMillis();
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

        return value.executeQuery();
    }

    public void executeUpdate() throws SQLException {
        value.executeUpdate();
    }


    public boolean needToclose() {
        return needToclose;
    }

    //open work with this statement
    public PreparedStatementBlocker block() {
        //not to be shared
        blocked = true;
        timeOfLife = System.currentTimeMillis();
        return this;
    }

    public boolean blocked() {
        return blocked;
    }


    //to close all work
    public void close() throws SQLException {

        if(needToclose) {
            //if new object was created
            if (!conn.isClosed()) conn.close();
            if (!value.isClosed())  value.close();
        }else{
            //release thish prep statement
            blocked = false;
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        value.setDate(i, date);
    }

    public void setDouble(int i, double locationLatitude) throws SQLException {
        value.setDouble(i, locationLatitude);
    }
}
