/*
 * DataAccessLayer
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
import notwa.wom.Context;
import notwa.wom.BusinessObject;

import java.util.Hashtable;
import notwa.exception.DalException;
import notwa.sql.ParameterSet;
import notwa.wom.BusinessObjectCollection;

/**
 * <code>DataAccessLayer</code> is an abstract class providing a basic functionality to all
 * inherited <code>DataAccessLayer</code>s.
 * It is intended to be constructed with given <code>ConnectionInfo</code> and
 * with given <code>Context</code> within it should live.
 * <p>It keeps a single instance of connection for every particular <code>ConnectionInfo</code>
 * so it is not creating two same instances of the same <code>ConnectionInfo</code>
 * during the life time of the application</p>
 * <p>The context awarness provides a way how to more efficiently maintain the
 * access to the database, for the requested entity could have been already requested
 * before, so it is not neccessary to ask the database for the data and recreate the entity again.
 * The <code>Context</code> instance hold all entities created within the same context,
 * in literal meaning.</p>
 * 
 * @author eTeR
 * @version %I% %G%
 */
public abstract class DataAccessLayer<Object extends BusinessObject, Collection extends BusinessObjectCollection> {
    private static Hashtable<ConnectionInfo, DatabaseConnection> connections;
    protected ConnectionInfo ci;
    protected Context currentContext;
    
    /**
     * The default constructor which should never been invoked.
     * Any attempt of constructing any DAL using this constructor will be logged
     * as an error!
     */
    protected DataAccessLayer() {
        LoggingInterface.getLogger().logWarning("Creating DataAccessLayer subclass with default constructor!");
    }
    
    /**
     * This is actually the default constructor which should be always used to 
     * create an instance of any DAL.
     *
     * @param ci    The <code>ConnectionInfo</code> which refers the actual database
     *              where we want to collect data from.
     * @param context   The actual <code>Context</code> where we want to let the DAL
     *                  live its pittyful life of collectiong data.
     */
    public DataAccessLayer(ConnectionInfo ci, Context context) {
        this.ci = ci;
        this.currentContext = context;

        if (connections == null) {
            connections = new Hashtable<ConnectionInfo, DatabaseConnection>();
        }
        if ((ci != null) && !connections.containsKey(ci)) {
            connections.put(ci, new DatabaseConnection(ci));
        }
    }

    /**
     * Retrieves the sole <code>DatabaseConnection</code> assigned to the
     * <code>ConnectionInfo</code> which was used to construct this instance of
     * DAL.
     *
     * @return The actual <code>DatabaseConnection</code>.
     */
    protected DatabaseConnection getConnection() {
        return connections.get(ci);
    }

    public abstract int Fill(Collection boc) throws DalException;
    public abstract int Fill(Collection boc, ParameterSet pc) throws DalException;
    public abstract Object get(ParameterSet primaryKey) throws DalException;
}
