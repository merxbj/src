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

import java.math.BigDecimal;
import java.sql.ResultSet;
import notwa.common.ConnectionInfo;
import notwa.logger.LoggingFacade;
import notwa.wom.Context;
import notwa.wom.BusinessObject;

import java.util.Hashtable;
import notwa.exception.DalException;
import notwa.sql.ParameterSet;
import notwa.sql.SqlBuilder;
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
 * @param <TObject> The <code>BusinessObject</code> concrete implementation to
 *                  be used as <code>BusinessObject</code> unit being created
 *                  by the concrete <code>DataAccessLayer</code>.
 *
 * @param <TCollection> The <code>BusinessObjectCollection</code> concrete implementation
 *                      to be filled by this concrete <code>DataAccessLayer</code>.
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class DataAccessLayer<TObject extends BusinessObject, TCollection extends BusinessObjectCollection<TObject>> {
    private static Hashtable<ConnectionInfo, DatabaseConnection> connections;

    /**
     * The <code>ConnectionInfo</code> which is used to connect to the 
     * database for this concrete <code>DataAcessLayer</code>
     */
    protected ConnectionInfo ci;

    /**
     * The <code>Context</code> within which this concrete <code>DataAcessLayer</code>
     * figuratively lives.
     */
    protected Context currentContext;

    /**
     * The default constructor which should never been invoked.
     * Any attempt of constructing any DAL using this constructor will be logged
     * as an error!
     */
    protected DataAccessLayer() {
        LoggingFacade.getLogger().logDebug("Creating DataAccessLayer subclass with default constructor!");
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

    /**
     * Fills the given <code>BusinessObjectCollection</code> with all possible data.
     *
     * @param boc The <code>BusinessObjectCollection</code> to fill.
     * @return The number of <code>BusinessObjects</code>s filled into the <code>Collection</code>.
     */
    public int fill(TCollection boc) {
        return fill(boc, new ParameterSet());
    }

    /**
     * Fills the given <code>BusinessObjectCollection</code> with data based on 
     * the given <code>ParameterSet</code>.
     * @see Parameters
     *
     * @param boc The <code>BusinessObjectCollection</code> to fill.
     * @param pc    The <code>ParameterSet</code> upon which the given collection
     *              will be filled.
     * @return The number of <code>BusinessObject</code>s filled into the <code>Collection</code>.
     */
    public int fill(TCollection boc, ParameterSet pc) {
        String sql = getSqlTemplate();
        SqlBuilder sb = new SqlBuilder(sql, pc);
        try {
            ResultSet rs = getConnection().executeQuery(sb.compileSql());
            boc.setClosed(false);
            boc.setResultSet(rs);

            while (rs.next()) {
                TObject bo = null;
                Object pk = getPrimaryKey(rs);
                if (isInCurrentContext(pk)) {
                    bo = getBusinessObject(pk);
                } else {
                    bo = getBusinessObject(pk, rs);
                }

                if (!boc.add(bo)) {
                    LoggingFacade.getLogger().logDebug("BusinessObject %s could not be added to the collection!", bo.toString());
                }
            }
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        } finally {
            /*
             * Make sure that the collection knows that it is up-to-date and close
             * it. This will ensure that any further addition/removal will be properly
             * remarked!
             */
            boc.setUpdateRequired(false);
            boc.setClosed(true);
        }
        
        return boc.size();
    }

    /**
     * Gets the single <code>BusinessObject</code> from the database based on the given
     * primary key.
     * If <code>BusinessObject> with the same primary key is already present in
     * the current <code>Context</code>, it is rather picked up from that context
     * instead of creating a new one and wasting the time with waiting for the
     * database to process the query.
     *
     * @param primaryKey It should be the primary key of requested <code>BusinessObject</code>.
     * @return  The requested <code>BusinessObject</code> instance.
     *          <code>null</code if there is no such a <code>BusinessObject</code>.
     * @throws DalException Whenever the given primaryKey actually isn't a
     *                      primary key or a database connection issue occures.
     */
    public TObject get(Object primaryKey) throws DalException {
        if (isInCurrentContext(primaryKey)) {
            return getBusinessObject(primaryKey);
        } else {
            String sql = getSqlTemplate();
            ParameterSet ps = getPrimaryKeyParams(primaryKey);
            SqlBuilder sb = new SqlBuilder(sql, ps);

            try {
                ResultSet rs = getConnection().executeQuery(sb.compileSql());

                if (!rs.last()) {
                    return null;
                }

                if (rs.getRow() == 1) {
                    return getBusinessObject(primaryKey, rs);
                } else {
                    throw new DalException("Supplied parameters are not a primary key!");
                }
            } catch (Exception ex) {
                throw new DalException(String.format("Unexpected error occured while getting a BusinessObject with primary key %s!", primaryKey.toString()), ex);
            }
        }
    }

    /**
     * Updates the database representation of the given concrete implementation
     * of <code>BusinessObjectCollection</code>.
     * <p>It is required that the given <code>BusinessObjectCollection</code>
     * has been built by this DAL, otherwise now action will be taken! Moreover,
     * it will skip the processing if there have been no changes made.</p>
     * <p>It utilizes already existing <code>ResultSet</code> used previously to
     * build the given <code>BusinessObjectCollection</code> to make the actuall
     * update/insert/delete againts the database.</p>
     *
     * @param boc   The <code>BusinessObjectCollection</code> which changes should
     *              be mirrored to the database.
     */
    public void update(TCollection boc) {
        if (boc.getResultSet() == null && !boc.isUpdateRequired()) {
            return;
        }

        ResultSet rs = boc.getResultSet();
        try {
            rs.beforeFirst();

            /*
             * Make sure that all existing rows gets updated
             */
            while (rs.next()) {
                Object primaryKey = getPrimaryKey(rs);
                TObject bo = boc.getByPrimaryKey(primaryKey);
                if (bo.isDeleted()) {
                    rs.deleteRow();
                } else if (bo.isUpdated()) {
                    updateSingleRow(rs, bo);
                    rs.updateRow();
                }
            }

            /*
             * Proceed with inserting of new rows
             */
            for (TObject bo : boc) {
                if (bo.isInserted()) {
                    /*
                     * ID higher than 1 mil is virtual, autogenerated, id which 
                     * is supposed to be replaced by the valid database id
                     */
                    if (bo.getUniqeIdentifier() >= 1000000) {
                        bo.setUniqeIdentifier(getLastUniqeIdentifier(bo) + 1);
                    }
                    rs.moveToInsertRow();
                    updateSingleRow(rs, bo);
                    rs.insertRow();
                    rs.moveToCurrentRow();
                }
            }

            /*
             * Commit when and only when we succesfuly pass the whole update process!
             */
            boc.commit();
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }

    /**
     * Refresh all the data contained in given <code>BusinessObjectCollection</code>.
     * At first, it completely clears the given </code>BusinessObjectCollection</code>
     * by calling the {@link BusinessObjectCollection#shakeAway()}. Then it simply
     * refills the <code>BusinessObjectCollection</code> from the database.
     * 
     * @param boc The <code>BusinessObjectCollection</code to be refreshed.
     */
    public void refresh(TCollection boc) {
        refresh(boc, new ParameterSet());
    }

    /**
     * Refresh all the data contained in given <code>BusinessObjectCollection</code>.
     * At first, it completely clears the given </code>BusinessObjectCollection</code>
     * by calling the {@link BusinessObjectCollection#shakeAway()}. Then it simply
     * refills the <code>BusinessObjectCollection</code> from the database taking
     * in account the given <coded>ParameterSet</code>.
     *
     * @param boc The <code>BusinessObjectCollection</code to be refreshed.
     * @param ps    The <code>ParameterSet</code> upon which the given collection
     *              will be refreshed.
     */
    public void refresh(TCollection boc, ParameterSet ps) {
        boc.shakeAway();
        fill(boc, ps);
    }

    /**
     * Acquires current highest valid uniqe identifier which is valid within the
     * database.
     *
     * @param bo    The <code>BusinessObject</code> we are looking the uniqe identifier
     *              for.
     * @return  The last valid value of uniqe identifier.
     * @throws DalException Whenever the acquired sql query actually isn't valid
     *                      or a database connection issue occures.
     */
    protected Integer getLastUniqeIdentifier(TObject bo) throws DalException {
        try {
            // TODO: Change this!
            return ((BigDecimal)getConnection().executeScalar(getHighestUniqeIdentifierSql(bo))).intValue();
        } catch (Exception ex) {
            throw new DalException(String.format("Unexpected error occured while getting a last uniqe identifier for %s!", bo.toString()), ex);
        }
    }

    /**
     * Gets the parametrized sql template which shall be provided by every concrete
     * implmentation of the DataAccessLayer.
     * <p>This sql actually describes how to obtain the concrete data from the
     * database and its used to build the <code>RecordSet</code> which is then
     * used to build the concrete <code>BusinessObjectCollection</code</p> and
     * the concrete implementation should know how it wants to build its data.
     *
     * @return  The actuall sql parametrized template to fill the concrete <code>
     *          BusinessObjectCollection</code> implementation.
     */
    protected abstract String getSqlTemplate();

    /**
     * Parses the concrete <code>BusinessObject</code> from the given <code>ResultSet</code>.
     * <p>This method is called only if the concrete <code>BusinessObject</code> has
     * not been found wihtin a current <code>Context</code> and the concrete
     * implementation should know how to obtain the requested data from the given
     * <code>ResultSet</code>
     *
     * @param primaryKey    The actuall primarky key identyfying the concrete
     *                      implementation of <code>BusinessObject</code>
     *
     * @param rs <code>ResultSet</code> where the data will be parsed from.
     * @return Built concrete implementation of <code>BusinessObject</code>.
     * @throws DalException Whenever error occures during the <code>ResultSet</code>
     *                      parsing, which should indicate that there is a column
     *                      missing in the database. This should be a rare occurance.
     */
    protected abstract TObject getBusinessObject(Object primaryKey, ResultSet rs) throws DalException;

    /**
     * Gets the concrete <code>BusinessObject</code> from the actual <code>Context</code>,
     * if there is one.
     * <p>This method always preceedes the {@link #getBusinessObject(java.lang.Object, java.sql.ResultSet)}
     * if the current <code>Context</code> contains the <code>BusinessObject</code>
     * identyfied by its primary key and the concrete implementation should know
     * how to obtain its concrete <code>BusinessObject</cpdo> from <code>Context</code>.</p>
     *
     * @param primaryKey    The actuall primary key identyfying the concrete
     *                      implementation of <code>BusinessObject</code>.
     * @return  Concrete implementation of <code>BusinessObject</code> acquired from
     *          the current <code>Context</code>.
     * @throws DalException Whenever error occures during the primary key recognition,
     *                      which should point to incorrect parameter passing. The
     *                      caller probably passed the primary key represented as
     *                      an unexpected datatype.
     */
    protected abstract TObject getBusinessObject(Object primaryKey) throws DalException;

    /**
     * Parses the primary key of the concrete <code>BusinessObject</code> from
     * the given <code>ResultSet</code>.
     *
     * @param rs    The <code>ResultSet</code> where the primary key shall be parsed
     *              from. The concrete implementation knows what is/are the primary
     *              key column(s).
     * @return  The parsed primary key.
     * @throws DalException Whenever the concrete implementation doesn't find the
     *                      expected columns in the given <code>ResultSet</code>.
     */
    protected abstract Object getPrimaryKey(ResultSet rs) throws DalException;

    /**
     * Checks whether the concrete implementation of <code>BusinessObject</code>
     * identyfied by the given primary key is not already present in the current
     * <code>Context</code>.
     * <p>The concrete implementation should know how to query the <code>Context</code>
     * for the concrete implementation of <code>BusinessObject</code>.</p>
     * 
     * @param primaryKey    The actuall primary key identyfying the concrete
     *                      implementation of <code>BusinessObject</code>.
     * @return  <code>true</code> if the queried concrete <code>BusinessObject</code>
     *          is already maintained within the current <code>Context</code>,
     *          <code>false</code> otherwise.
     * @throws DalException Whenever the concrete implementation doesn't find the
     *                      expected columns in the given <code>ResultSet</code>.
     */
    protected abstract boolean isInCurrentContext(Object primaryKey) throws DalException;

    /**
     * Builds the <code>ParameterSet</code> which will then identify the one and
     * only concrete <code>BusinessObject</code> when there is a need to query
     * such a <code>BusinessObject</code> from the database.
     * <p>The concrete implementation shoudl know how exactly build the <code>ParameterSet</code>
     * using the given primary key.</p>
     * @param primaryKey    The actuall primary key identyfying the concrete
     *                      implementation of <code>BusinessObject</code>.
     * @return  The built <code>ParameterSet</code> which could then by used to
     *          obtain the one and only <code>BusinessObject</code> from database.
     */
    protected abstract ParameterSet getPrimaryKeyParams(Object primaryKey);

    /**
     * Updates the single row in the given <code>ResultSet</code> based on the
     * given concrete implementation of <code>BusinessObject</code>.
     * <p>The concrete implementation should know which columns should be updated
     * based on which values from the given <code>BusinessObject</code>.</p>
     *
     * @param rs The <code>ResultSet</code> to be updated.
     * @param bo    The <code>BusinessObject</code> to be used to update the given
     *              <code>ResultSet</code>.
     * @throws Exception    Whenever the concrete implementation doesn't find the
     *                      expected columns in the given <code>ResultSet</code>.
     */
    protected abstract void updateSingleRow(ResultSet rs, TObject bo) throws Exception;

    /**
     * Gets the sql query that should return the current highest value of uniqe
     * identifier.
     * The query pattern should look like the following example:
     * <code>SELECT primary_key_column FROM table SORT BY primary_key_column DESC</code>
     *
     * @param bo <code>BusinessObject</code> for which we are building the query.
     * @return The requested query.
     */
    protected abstract String getHighestUniqeIdentifierSql(TObject bo);
}
