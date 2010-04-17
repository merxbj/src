/*
 * BusinessObjectCollection
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
package notwa.wom;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import notwa.exception.ContextException;
import notwa.exception.DeveloperException;

/**
 * Abstract class providing a general behavior connected with maintaining a
 * collection of <code>BusinessObject</code>s.
 * <p>This collection is usualy build upon the data queried from the database.</p>
 * 
 *
 * @author Tomas Studnicka
 * @author Jaroslav Merxbauer
 * @param <T>   The concrete implemntation of <code>BusinessObject</code> to be hold
 *              by this collection.
 */
public abstract class BusinessObjectCollection<T extends BusinessObject> implements Iterable<T> {

    protected ArrayList<T> collection;
    protected Context currentContext;
    protected ResultSet resultSet;
    protected boolean closed;
    protected boolean updateRequired;

    /**
     * The sole basic constructor providing a potential of setting the current
     * <code>Context</code> and the initial <code>ResultSet</code> where this
     * collection have came from.
     * 
     * @param currentContext    The actual <code>Context</code> where this 
     * <code>BusinessObjectCollection</code> lives.
     * @param resultSet The original <code>ResultSet</code> based which this
     *                  <code>BusinessObjectCollection</code> has been created.
     */
    public BusinessObjectCollection(Context currentContext, ResultSet resultSet) {
        this.currentContext = currentContext;
        this.resultSet = resultSet;
        this.collection = new ArrayList<T>();
    }

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }

    /**
     * Attempts to add the given <code>BusinessObject</code> to this 
     * <code>BusinessObjectCollection</code>. It is required that:
     * <ul>
     * <li> Both <code>BusinessObject</code> and <code>BusinessObjectCollection</code>
     *      are <code>Context</code> aware.</li>
     * <li> The given <code>BusinessObject</code> isn't already present in the
     *      <code>BusinessObjectCollection</code>.</li>
     * </ul>
     * If this <code>BusinessObjectCollection</code> has been already closed,
     * which means that we are not building this <code>BusinessObjectCollection</code>
     * but rather updating, we will mark this collection to require update and
     * sets the <code>inserted</code> flag of <code>BusinessObject</code> to <code>true</code>.
     *
     * @param bo <code>BusinessObject</code> to be added to this <code>BusinessObjectCollection</code>
     * @return <code>true</code> if the addition success, <code>false</code> otherwise
     * @throws ContextException If either the <code>BusinessObject</code> or <code>BusinessObjectCollection</code>
     *                          are not <code>Context</code> aware, or they live in different <code>Context</code>s.
     */
    public boolean add(T bo) throws ContextException {
        /*
         * Make sure that we are context aware and both BusinessObject and
         * BusinessObjectCollection lives in the same context!
         */
        if ((bo.getCurrentContext() == null) || (this.getCurrentContext() == null) ||
                !bo.getCurrentContext().equals(this.getCurrentContext())) {
            throw new ContextException("BusinessObject lives in another context than BusinessObjectCollection!");
        }

        /*
         * If the given BO is id-less, acquire correct temporary index for it
         */
        if (!bo.hasUniqeIdentifier()) {
            acquireUniqeIdentifier(bo);
        }

        /*
         * Make sure that the same BusinessObject isn't already present in the
         * collection.
         */
        if (collection.contains(bo)) {
            return false;
        }

        /*
         * Try to add the BusinesObject to the BusinessObjectCollection.
         */
        if (!collection.add(bo)) {
            return false;
        }

        
        /*
         * Attach BusinessObject with this BusinessObjectCollection togeather
         */
        bo.attach(this);

        /*
         * If this collection is already closed, which means that we cannot freely
         * add/remove BusinessObjects from it, perform appropriate actions to
         * remark that this new item needs to be inserted to the database during
         * a database update
         */
        if (isClosed()) {
            bo.setInserted(true);
            setUpdateRequired(true);
        }

        /*
         * If we have reached this point we have been able to add the BusinessObject
         * to the BusinessObjectCollection
         */
        return true;
    }
    
    /**
     * Marks the given <code>BusinessObject</code> for deletion from this 
     * <code>BusinessObjectCollection</code> if this is already closed <code>BusinessObjectCollection</code>,
     * oterwise it will delete it physicaly from this <code>BusinessObjectCollection</code>.
     * <p>It is required that:
     * <ul>
     * <li> Both <code>BusinessObject</code> and <code>BusinessObjectCollection</code>
     *      are <code>Context</code> aware.</li>
     * </ul>
     * </p>
     * 
     * @param bo    <code>BusinessObject</code> to be mark for removal from this
     *              <code>BusinessObjectCollection</code>
     * @return <code>true</code> if the mark success, <code>false</code> otherwise
     * @throws ContextException If either the <code>BusinessObject</code> or <code>BusinessObjectCollection</code>
     *                          are not <code>Context</code> aware, or they live in different <code>Context</code>s.
     */
    public boolean remove(T bo) throws ContextException {
        /*
         * Make sure that we are context aware and both BusinessObject and
         * BusinessObjectCollection lives in the same context!
         */
        if ((bo.getCurrentContext() == null) || (this.getCurrentContext() == null) ||
                !bo.getCurrentContext().equals(this.getCurrentContext())) {
            throw new ContextException("BusinessObject lives in another context than BusinessObjectCollection!");
        }

        /*
         * Make sure that the  BusinessObject is present in the collection.
         */
        if (!collection.contains(bo)) {
            return false;
        }

        /*
         * If this collection is already closed and the BusinessObject hasn't
         * been previously inserted to the collection (so there is no need for
         * a database update, mark the item as deleted and make sure that this
         * collection is aware of that change to be updated to the database.
         * Otherwise, just remove the BusinessObject from this collection
         * and detach it.
         */
        if (isClosed() && !bo.isInserted()) {
            bo.setDeleted(true);
            setUpdateRequired(true);
        } else {
            collection.remove(bo);
            bo.detach();
        }

        /*
         * If we have reached this point, we can leave with true.
         */
        return true;
    }
    
    /**
     * Gets the number of the elements contained in the 
     * <code>BusinessObjectCollection</code>.
     *
     * @return The actual number of elements.
     */
    public int size() {
        return collection.size();
    }
    
    /**
     * Gets the element contained in the <code>BusinessObjectCollection</code> on
     * the given index.
     *
     * @param index Index where to pick-up the <code>BusinessObject</code>.
     * @return  The <code>BusinessObject</code> present on the position represented
     *          by the given index.
     */
    public T get(int index) {
        return (T) collection.get(index);
    }

    /**
     * Shake away all <code>BusinessObject</code>s attached to this 
     * <code>BusinessObjectCollection</code>. This factically means:
     * <ol>
     * <li>Detach all <code>BusinessObject</code>s from colletion</li>
     * <li>Clear this collection</li>
     * <li>Clear the current context</li>
     * </ol>
     */
    public void shakeAway() {
        for (BusinessObject bo : collection) {
            bo.detach();
        }
        collection.clear();
        currentContext.clear();
    }

    /**
     * Gets the <code>Context</code> where this collection currently lives.
     *
     * @return The current <code>Context</code>
     */
    public Context getCurrentContext() {
        return currentContext;
    }

    /**
     * Sets the <code>Context</code> where this collection is going to live.
     * 
     * @param currentContext The context to live in.
     */
    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    /**
     * Removes all <code>BusinessObject</code> marked for deletion from this
     * <code>BusinessObjectCollection</code> and commits all changes for the others.
     */
    public void commit() {
        ArrayList<BusinessObject> garbage = new ArrayList<BusinessObject>();
        for (BusinessObject bo : collection) {
            if (bo.isDeleted()) {
                garbage.add(bo);
            } else {
                bo.commit();
            }
        }
        collection.removeAll(garbage);
        setUpdateRequired(false);
    }

    /**
     * Rollbacks all changes made to this <code>BusinessObjectCollection</code>.
     */
    public void rollback() {
        for (BusinessObject bo : collection) {
            bo.rollback();
        }
        setUpdateRequired(false);
    }

    /**
     * Sets this <code>BusinesObjectCollection</code> as closed which actually means
     * that the current version is a mirror image of the data queried from the database.
     * <p>This version is then changed over the time:
     * <ul>
     * <li>Data are modified - database update</li>
     * <li>Data are inserted - database insert</li>
     * <li>Data are deleted  - database delete</li>
     * </ul>
     * <code>BusinessObjectCollection</code> is aware of such a changes while it is
     * being closed and makes sure that all <code>BusinessObject</code>s are aware
     * of their current status (deleted/inserted) or the <code>BusinessObject</code>
     * may query this status using <code>isClosed</code> and could remark that it 
     * is changed.</p>
     * <p>And as soon as the user performs an action which forces the data to be
     * saved to the database, this <code>BusinessObjectCollection</code> could take
     * appropriate steps to update the database appropriately.</p>
     * 
     * @param   closed <code>true</code> if this <code>BusinessObjectCollection</code>
     *          should have been closed, <code>false</code> otherwise.
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Checks whether this <code>BusinesObjectCollection</code> is closed which actually means
     * that the current version is a mirror image of the data queried from the database.
     * <p>This version is then changed over the time:
     * <ul>
     * <li>Data are modified - database update</li>
     * <li>Data are inserted - database insert</li>
     * <li>Data are deleted  - database delete</li>
     * </ul>
     * <code>BusinessObjectCollection</code> is aware of such a changes while it is
     * being closed and makes sure that all <code>BusinessObject</code>s are aware
     * of their current status (deleted/inserted) or the <code>BusinessObject</code>
     * may query this status using this method and could remark that it is changed.</p>
     * <p>And as soon as the user performs an action which forces the data to be
     * saved to the database, this <code>BusinessObjectCollection</code> could take
     * appropriate steps to update the database appropriately.</p>
     * 
     * @return  <code>true</code> if this <code>BusinessObjectCollection</code> is
     *          already closed, <code>false</code> otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Checks whether this <code>BusinessObjectCollection</code> have been updated
     * since it was marked as closed.
     * @see #isClosed()
     *
     * @return  <code>true</code> if this <code>BusinessObjectCollection</code> differs
     *          from its database representation, <code>false</code> otherwise.
     */
    public boolean isUpdateRequired() {
        return updateRequired;
    }

    /**
     * Sets whether this <code>BusinessObjectCollection</code> have been updated
     * since it was marked as closed.
     * @see #isClosed()
     *
     * @param updateRequired    <code>true</code> if this <code>BusinessObjectCollection</code>
     *                          differs from its database representation,
     *                          <code>false</code> otherwise.
     */
    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    /**
     * Sets the result set upon which this collection is build and through which
     * the database can be easily updated.
     * 
     * @param resultSet The original <code>ResultSet</code> based which this
     *                  <code>BusinessObjectCollection</code> has been created.
     */
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * Gets the result set upon which this collection is build and through which
     * the database can be easily updated.
     *
     * @return resultSet The original <code>ResultSet</code> based which this
     *                  <code>BusinessObjectCollection</code> has been created.
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Notifies this <code>BusinessObjectCollection</code> that its owned
     * <code>BusinessObject</code> has been updated.
     * This results in marking this collection as well as the object to be updated.
     *
     * @param businessObject    The actuall <code>BusinessObject</code> that has
     *                          been updated.
     */
    public void setUpdated(T businessObject) {
        setUpdateRequired(true);
        businessObject.setUpdated(true);
    }

    /**
     * Gets the concrete implementation of <code>BusinessObject</code> by its
     * primary key.
     * <p>The concrete implementation should know how to handle the given primary
     * key</p>
     * @param primaryKey    The actuall primary key which should be used to the
     *                      <code>BusinessObject</code> lookup.
     * @return  The found concrete implementation of the <code>BusinessObject</code>,
     *          <code>null</code> if there is none such.
     * @throws DeveloperException   If the developer haven't precisely specified the
     *                              comparing methods for the concrete implementation
     *                              of <code>BusinessObject</code>.
     */
    public abstract T getByPrimaryKey(Object primaryKey) throws DeveloperException;

    /**
     * Provides the given concrete implementation of <code>BusinessObject</code>
     * with valid temporary uniqe identifiyer.
     * This is going to be replaced with valid one after the database update.
     *
     * @param bo    Concrete implementation of <code>BusinessObject</code> to be
     *              provided with uniqe identifier.
     */
    protected abstract void acquireUniqeIdentifier(T bo);

}
