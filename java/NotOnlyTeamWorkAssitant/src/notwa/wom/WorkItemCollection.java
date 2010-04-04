/*
 * WorkItemCollection
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
import java.util.Collections;
import notwa.exception.DeveloperException;

/**
 * This class represents a concrete implmenetation of <code>BusinessObjectCollection</code>
 * keeping and maintaining the <code>WorkItem</code>s.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WorkItemCollection extends BusinessObjectCollection<WorkItem> {

    /**
     * The default constructor setting the current <code>Context</code> and <code>
     * ResultSet</code> to <code>null</code>.
     */
    public WorkItemCollection() {
        super(null, null);
    }

    /**
     * The constructor setting the current <code>Context</code> according to the 
     * given value and <code>ResultSet</code> to <code>null</code>.
     */
    public WorkItemCollection(Context context) {
        super(context, null);
    }

    /**
     * The constructor setting the current <code>Context</code> and <code>ResultSet</code> 
     * to according to the given values.
     */
    public WorkItemCollection(Context currentContext, ResultSet resultSet) {
        super(currentContext, resultSet);
    }

    @Override
    public WorkItem getByPrimaryKey(Object primaryKey) throws DeveloperException {
        int workItemIndex;
        try {
            workItemIndex = Collections.binarySearch(collection, new WorkItem((Integer) primaryKey));
            if (workItemIndex >= 0) {
                return super.get(workItemIndex);
            } else {
                return null;
            }
        } catch (ClassCastException ccex) {
            throw new DeveloperException("Developer haven't provided correct comparing and equaling methods for WorkItem!", ccex);
        }
    }

}
