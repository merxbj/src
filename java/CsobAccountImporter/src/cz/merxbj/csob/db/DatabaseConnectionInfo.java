/*
 * DatabaseConnectionInfo
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

package cz.merxbj.csob.db;

/**
 * DatabaseConnectionInfo encapsulates all required connection information to be able
 * to connect to the database with the proper credetials. These values are:
 * <ul>
 * <li>Host name</li>
 * <li>Port number</li>
 * <li>Database name</li>
 * <li>User name</li>
 * <li>Password</li>
 * </ul>
 * DatabaseConnectionInfo also encapsulates the label which might be used to distinguish
 * between several connections easily.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class DatabaseConnectionInfo extends ConnectionInfo {
    private String dbname;
    private String user;
    private String password;

    /**
     * Implemntation of the {@link Comparable} interface.
     * Compares two <code>DatabaseConnectionInfo</code> subsequently by their:
     * <ol>
     * <li>Host name</li>
     * <li>Port number</li>
     * <li>Database name</li>
     * <li>User name</li>
     * </ol>
     *
     * @param ci The actual <code>DatabaseConnectionInfo</code> to be compared with.
     * @return  The usual compareTo value resulting from the latest non-zero
     *          comparsion based on the orded described above.
     */
    @Override
    public int compareTo(ConnectionInfo ci) {
        DatabaseConnectionInfo dci = (DatabaseConnectionInfo) ci;
        if (dci != null) {
            int compare = super.compareTo(dci);
            if (compare == 0) {
                compare = (dci.dbname != null) ? dbname.compareTo(dci.dbname) : 0;
            }
            if (compare == 0) {
                compare = (dci.user != null) ? user.compareTo(dci.user) : 0;
            }
            return compare;
        }
        return 1;
    }

    /**
     * Compares the given <code>Object</code> with the actual instance.
     * It utilizes the <code>compareTo</code> method the decrease the coding
     * and maintenance effort.
     *
     * @param obj The actual <codee>Object</code> to be compared with.
     * @return  <code>true</code> if both <code>Object</code> are the same in terms of values
     *          <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof DatabaseConnectionInfo)) {
            return false;
        } else {
            return this.compareTo((DatabaseConnectionInfo) obj) == 0 ;
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 53 * hash + (this.dbname != null ? this.dbname.hashCode() : 0);
        return hash;
    }
    
    /**
     * Sets the database name where the data for this application is stored.
     * <p>Please make sure that the database you are connecting to matches the
     * schema which is expected by the actual version of the NOTWA application.</p>
     * 
     * @param dbname The actual database name.
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }
    
    /**
     * Sets the user name which should have the permission to access the database.
     * <p>Please make sure that the requested user has all permission available
     * to work with the choosen database</p>
     * 
     * @param user The actual user name.
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    /**
     * Sets the password, which is not encrypted, yet, assigned to the choosen
     * user.
     * 
     * @param password The actual, non-encrypted, password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password, which is not encrypted, yet, assigned to the choosen
     * user.
     * 
     * @return The actual, non-encrypted, password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user name which should have the permission to access the database.
     * <p>Please make sure that the requested user has all permission available
     * to work with the choosen database</p>
     * 
     * @return The actual user name.
     */
    public String getUser() {
        return user;
    }

    public String getDbname() {
        return dbname;
    }

    /**
     * Build the connection string which is accepted by the {@link jdbc} to
     * connect to the requested database.
     * <p>Currently only the mysql is supported and therefore assumed to be used
     * as a part of the resulted connection string.</p>
     * <p>The connections string also specifies the method how to work with zero
     * <code>DateTime</code> values stored in the database. Without this
     * specification, the <code>getTimestamp</code> method would throw an <code>Exception</code>
     * which would make the <code>ResultSet</code> processing obscure.
     *
     * @return The actual connection string in format:
     *         <p><code>jdbc:mysql://host:port/dbname?options</code></p>
     */
    public String compileConnectionString() {
        String cs = String.format("jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull",
                this.getHost(),
                this.getPort(),
                this.getDbname());
        return cs;
    }
}
