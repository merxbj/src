/*
 * BusinessObject
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

import java.lang.reflect.Field;
import notwa.logger.LoggingFacade;
import notwa.exception.ContextException;

/**
 * Abstract class representing a single <code>BusinessObject</code> and providing
 * a common behavior to be shared by all possible ascendants.
 * <p><code>BusinessObject</code>s could be maintained simply as regular objects
 * without no rules attached to them. However, as soon as they are going to be maintained
 * as a part of <code>BusinessObjectCollection</code> they must obey certain rules:
 * <ul>
 * <li>They must be context aware, which means that they must have <code>Context</code>
 * withing they live</li>
 * <li>They will be <code>attach</code>ed to the collection and they then cannost perform
 * freely certain operations</li>
 * <ul></p>
 *
 * @author Tomas Studnicka
 * @author Jaroslav Mexbauer
 */
public abstract class BusinessObject {
    
    /**
     * The <code>BusinessObjectCollection</code> we are attached to.
     */
    protected BusinessObjectCollection attachedBOC;
    
    /**
     * The original version of this <code>BusinessObject</code> which could be
     * picked up and replaced with the current state by calling the {@link #rollback()}.
     */
    protected BusinessObject originalVersion;

    /**
     * The current <code>Context</code> we are living within. This <code>Context</code>
     * keeps all the <code>BusinessObjects</code> related to our literal context.
     */
    protected Context currentContext;

    /**
     * The flag indicating that the content of this <code>BusinessObject</code> have
     * been updated.
     *
     * <p>Please note that this flag has sense only as soon as this <code>BusinessObject</code>
     * is attached ({@link #attach(notwa.wom.BusinessObjectCollection)}) to the
     * closed ({@link BusinessObjectCollection#closed}) <code>BusinessObjectCollection</code>.
     * </p>
     */
    protected boolean updated;

    /**
     * The flag indicating that this <code>BusinessObject</code> have been marked
     * for deletion.
     *
     * <p>Please note that this flag has sense only as soon as this <code>BusinessObject</code>
     * is attached ({@link #attach(notwa.wom.BusinessObjectCollection)}) to the
     * closed ({@link BusinessObjectCollection#closed}) <code>BusinessObjectCollection</code>.
     * </p>
     */
    protected boolean deleted;
    
    /**
     * The flag indicating that this <code>BusinessObject</code> has been newly
     * added to the attached <code>BusinessObjectCollection</code>.
     *
     * <p>Please note that this flag has sense only as soon as this <code>BusinessObject</code>
     * is attached ({@link #attach(notwa.wom.BusinessObjectCollection)}) to the
     * closed ({@link BusinessObjectCollection#closed}) <code>BusinessObjectCollection</code>.
     * </p>
     */
    protected boolean inserted;

    /**
     * The sole constructor making sure that all members are set to their default
     * values.
     */
    public BusinessObject() {
        this.deleted = false;
        this.inserted = false;
        this.updated = false;
        this.currentContext = null;
        this.originalVersion = null;
    }

    /**
     * Attach Business object to <code>BusinessObjectCollection</code>.
     * <p>As soon as the <code>BusinessObject</code> is attached certain behavior
     * is restricted and this <code>BusinessObject</code> is well recognized as
     * and part of its collection.</p>
     * <p>It also helps to hold the bidirectional information of who is keeping
     * who and who is being kept by who.</p>
     *
     * @param boc The attached <code>BusinessObjectCollection</code>.
     */
    public void attach(BusinessObjectCollection boc) {
        this.attachedBOC = boc;
        try {
            this.originalVersion = (BusinessObject) this.clone();
        } catch (CloneNotSupportedException ex) {
            LoggingFacade.handleException(ex);
        }
    }
    
    /**
     * Detach <code>BusinessObject</code> from <code>BusinessObjectCollection</code>.
     * <p>As soon as the <code>BusinessObject</code> is detached, its connection
     * with the attached <code>BusinessObjectCollection</code> would become too lose
     * and therefore it is removed from it.</p>
     */
    public void detach() {
        try {
            this.attachedBOC.remove(this);
            this.attachedBOC = null;
        } catch (ContextException cex) {
            LoggingFacade.handleException(cex);
        }
    }

    /**
     * Check if <code>BusinessObject</code> is attached to <Code>BusinessObjectCollection</code>.
     *
     * @return  <code>true</code> if this <code>BusinessObject</code> is attached
     *          to any <code>BusinessObjectCollection</code>.
     */
    public boolean isAttached() {
        return (attachedBOC != null);
    }
    
    /**
     * Rewrites all user changes with original version
     */
    public void rollback() {
        Class<?> c = this.getClass();
        Class<?> o = originalVersion.getClass();
        
        for (Field field : c.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Field ovField = o.getDeclaredField(field.getName());
                ovField.setAccessible(true);
                   field.set(this, ovField.get(originalVersion));
            } catch (Exception e) {
                LoggingFacade.handleException(e);
            }
        }
    }
    
    /**
     * Saves all user changes to original version and clears the inserted flag.
     */
    public void commit() {
        this.originalVersion = null;
        try {
            this.originalVersion = (BusinessObject) this.clone();
        } catch (CloneNotSupportedException e) {
            LoggingFacade.handleException(e);
        }

        this.setInserted(false);
    }

    /**
     * Sets the current <code>Context<code> which represents the actual <code>context</code>
     * within this business is going to live.
     *
     * @param currentContext The actual <code>Context<code> to be registered with.
     */
    public abstract void registerWithContext(Context currentContext);

    /**
     * Gets the <code>Context</code> representing the actuall context within this 
     * business object has been created.
     * <p>Only <code>BusinessObject</code>s within the same context could relate
     * with each other</p>
     *
     * @return The current context.
     */
    public Context getCurrentContext() {
        return currentContext;
    }

    /**
     * Checks whether this <code>BusinessObject</code> wasn't already marked as
     * deleted which could draw some restriction to certain oprations which is
     * working with it.
     * <p>The physical representation in the <code>BusinessObjectCollection</code>
     * is going to be removed as soon as the <code>commit</code> method is invoked
     * usualy by the <code>DataAccessLayer.</code></p>
     *
     * @return  <code>true</code> if this <code>BusinessObject</code> has been already
     *          marked for deletion, <code>false</code> otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Marks this <code>BusinessObject</code> for deletion which could draw some
     * impact to certain oprations which is working with it.
     *
     * <p>The physical representation in the <code>BusinessObjectCollection</code>
     * is going to be removed as soon as the <code>commit</code> method is invoked
     * usualy by the <code>DataAccessLayer</code>.</p>
     * 
     * @param deleted   Indicating whether this <code>BusinessObject</code> will
     *                  be marked for deletion or not.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Checks wheter this <code>BusinessObject</code> is marked as inserted in the 
     * already closed <code>BusinessObjectCollection</code> which means that this 
     * object doesn't already have a representation in the database.
     *
     * <p>This mark is going to be removed as soon as the <code>BusinessObjectCollection</code>
     * invokes the <code>commit</code> method usualy by the <code>DataAccessLayer</code>.</p>
     *
     * @return <code>true</code> if this <code>BusinessObject</code> is marked as
     * inserted, <code>false</code> otherwise.
     */
    public boolean isInserted() {
        return inserted;
    }

    /**
     * Marks this <code>BusinessObject</code> as inserted to the already closed
     * <code>BusinessObjectCollection</code> which means that this object doesn't
     * already have a representation in the database.
     *
     * <p>This mark is going to be removed as soon as the <code>BusinessObjectCollection</code>
     * invokes the <code>commit</code> method usualy by the <code>DataAccessLayer</code>.</p>
     *
     * @param inserted  Indicating whether this <code>BusinessObject</code> will
     *                  be marked as inserted or not.
     */
    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    /**
     * Gets whether this <code>BusinessObject</code> has been updated since it has
     * become a member of a <code>BusinessObjectCollection</code>.
     * This flag is removed as soon as the {@link #commit()} method is called to
     * facticaly save the updated information.
     * 
     * @return  <code>true</code> if this <code>BusinessObject</code> has been updated,
     *          <code>false</code> otherwise.
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Sets whether this <code>BusinessObject</code> has been updated since it has
     * become a member of a <code>BusinessObjectCollection</code>.
     * This flag is removed as soon as the {@link #commit()} method is called to
     * facticaly save the updated information.
     *
     * @param updated   <code>true</code> if this <code>BusinessObject</code> has 
     *                  been updated, <code>false</code> otherwise.
     */
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    /**
     * Gets whether the queried <code>BusinessObject</code> is uniqely indentified.
     * This means whether it has it's id. If not, it is up to the
     * <code>BusinessObjectCollection</code> to acquire it.
     *
     * @return  <code>true</code> if the item has not-null or non-zero id, <code>false</code
     *          otherwise.
     */
    public abstract boolean hasUniqeIdentifier();

    /**
     * Sets the uniqe identifier of the concrete implementation of this
     * <Code>BusinessObject</code>.
     *
     * @param value The uniqe identifier.
     */
    public abstract void setUniqeIdentifier(int value);

    /**
     * Gets the uniqe identifier of the concrete implementation of this
     * <Code>BusinessObject</code>.
     *
     * @return The uniqe identifier.
     */
    public abstract int getUniqeIdentifier();
}
