/*
 * DatabaseConnection
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>DatabaseConnection</code> encapsulates the jdbc conneciton retrived
 * by the {@link DriverManager}. It provides a unified way to construct and 
 * open the connection and to execte queries on it.
 *
 * @author eTeR
 * @version %I% %G%
 */
public class DatabaseConnection {
    private Connection con;
    private ConnectionInfo ci;
    
    /**
     * The sole constructor accepting the <code>ConnectionInfo</code> describing
     * the actual database and credentials to which we are trying to connect.
     *
     * @param ci    The <code>ConnectionInfo</code> keeping the database server connection
     *              information and credetials.
     */
    public DatabaseConnection(ConnectionInfo ci) {
        this.ci = ci;
    }
    
    /**
     * Executes the given SQL Query and returns the result kept by the <code>ResultSet</code>.
     * The caller is responsible to know what type every column should be and call the
     * appropriate strongly typed method.
     *
     * @param query The SQL Query that should be executed againts the server.
     * @return  The actual <code>ResultSet</code> filled by the executed query.
     * @throws SQLException If there is a problem with the database connection or
     *                      whenever there is a problem with retrieving data from
     *                      the database or accessing the ResultSet.
     */
    public ResultSet executeQuery(String query) throws Exception {
        /* Make sure that you are connected, otherwise try to reconnect */
        if (!isConnected()) {
            connect();
        }
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query);
        return rs;
    }

    /**
     * Executes the given SQL Query and returns a single scalar value.
     * The caller is responsible to know what type it should be and do the
     * appropriate type-cast.
     *
     * @param query The SQL Query that should be executed againts the server.
     *              Please make sure that this query result to one row with a
     *              single column to acquire the correct data.
     * @return  <code>Object</code> value from the first column of the first row which is 
     *          suposed to be the only returned variable.
     *          <code>null</code> if there is no row.
     * @throws SQLException If there is a problem with the database connection or
     *                      whenever there is a problem with retrieving data from
     *                      the database or accessing the ResultSet.
     */
    public Object executeScalar(String query) throws SQLException {
        /* Make sure that you are connected, otherwise try to reconnect */
        if (!isConnected()) {
            connect();
        }

        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query);
        if (rs.next()) {
            return rs.getObject(1);
        } else {
            return null;
        }
    }

    /**
     * Verifies whether the connection wasn't already closed either manually by the
     * <code>close</code> method or due to some network problem.
     * If the connection haven't been even already got, it is consider at not connected.
     * @return  <code>true</code> if there is a connection ongoing between the application
     *          and the database server.
     *          <code>false</code> otherwise.
     */
    public boolean isConnected() {
        try {
            if (con != null) {
                return !this.con.isClosed();
            } else {
                return false;
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
            return false;
        }
    }

    /**
     * Attempts to close the opened connection.
     */
    public void close() {
        if (isConnected()) {
            try {
                con.close();
            } catch (Exception ex) {
                LoggingInterface.getInstanece().handleException(ex);
            }
        }
    }

    /**
     * Attempts to connect to the database server provided by the <code>ConnectionInfo</code>.
     * 
     * @throws SQLException When a critical issue occured during the connection process.
     */
    private void connect() throws SQLException {
        try {
            con = DriverManager.getConnection(ci.compileConnectionString(), ci.getUser(), ci.getPassword());
        } catch (SQLException ex) {
            LoggingInterface.getInstanece().handleException(ex);
            throw ex;
        }
    }
}
