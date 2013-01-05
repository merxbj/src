/*
 * ConnectionInfo
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
 *
 * @author eTeR
 * @version %I% %G%
 */
public class ConnectionInfo implements Comparable<ConnectionInfo> {
    private String label;
    private String host;
    private String port;
    
    /**
     * Sets the label of this <code>ConnectionInfo</code> instance.
     * This label could be used to easily distguish between several different
     * instances.
     * 
     * @param label The actual label which should be as much descriptive as
     *              possible to easily identify this <code>ConnectionInfo</code>.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Sets the host name where the desired database server is running.
     * The host name could be either
     * <ul>
     * <li>IP adress or</li>
     * <li>URL which could be translated into the actual IP adress</li>
     * </ul>
     * 
     * @param host The IP adress or URL of the database server.
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    /**
     * Sets the port number where the desired database server is listining.
     * 
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }
    
    /**
     * Gets the label of this <code>ConnectionInfo</code> instance.
     * This label could be used to easily distguish between several different
     * instances.
     * 
     * @return  The actual label which should be as much descriptive as
     *          possible to easily identify this <code>ConnectionInfo</code>.
     */
    public String getLabel() {
        return this.label;
    }
    
        
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    @Override
    public int compareTo(ConnectionInfo ci) {
        int compare = (ci.label != null) ? label.compareTo(ci.label) : 0;
        if (compare == 0) {
            compare = (ci.host != null) ? host.compareTo(ci.host) : 0;
        }
        if (compare == 0) {
            compare = (ci.port != null) ? port.compareTo(ci.port) : 0;
        }
        return compare;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectionInfo)) {
            return false;
        } else {
            ConnectionInfo other = (ConnectionInfo) obj;
            if (other != null) {
                return this.compareTo(other) == 0;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 53 * hash + (this.port != null ? this.port.hashCode() : 0);
        return hash;
    }

}
