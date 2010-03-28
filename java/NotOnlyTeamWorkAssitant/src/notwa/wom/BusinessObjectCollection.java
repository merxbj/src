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

/**
 * Abstract class providing an general behavior connected with maintaining a
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

    /**
     * The sole basic constructor providing a potential of setting the current
     * <code>Context</code> and the initial <code>ResultSet</code> which this 
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
         * Make sure that the same BusinessObject isn't already present in the
         * collection.
         */
        if (collection.contains(bo)) {
            return false;
        }

        /*
         * Try to add the BusinesObject to the BusinessObjectCollection and
         * attach them together.
         */
        if (collection.add(bo)) {
            bo.attach(this);
            return true;
        }

        /*
         * If we have reached this point we were not able to add the BusinessObject
         * to the BusinessObjectCollection
         */
        return false;
    }
    
    /**
     * Marks the given <code>BusinessObject</code> for deletion from this 
     * <code>BusinessObjectCollection</code>. It is required that:
     * <ul>
     * <li> Both <code>BusinessObject</code> and <code>BusinessObjectCollection</code>
     *      are <code>Context</code> aware.</li>
     * </ul>
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

        bo.setDeleted(true);
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
    }

    /**
     * Rollbacks all changes made to this <code>BusinessObjectCollection</code>.
     */
    public void rollback() {
        for (BusinessObject bo : collection) {
            bo.rollback();
        }
    }
}
