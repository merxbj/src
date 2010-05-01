/*
 * ProjectCollection
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
 * keeping and maintaining the <code>Projects</code>s.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ProjectCollection extends BusinessObjectCollection<Project> {

    private static int nextProjectId = 1000000;

    /**
     * The default constructor setting the current <code>Context</code> and <code>
     * ResultSet</code> to <code>null</code>.
     */
    public ProjectCollection() {
        super(null, null);
    }

    /**
     * The constructor setting the current <code>Context</code> according to the 
     * given value and <code>ResultSet</code> to <code>null</code>.
     *
     * @param context The current <code>Context</code>.
     */
    public ProjectCollection(Context context) {
        super(context, null);
    }

    /**
     * The constructor setting the current <code>Context</code> and <code>ResultSet</code> 
     * to according to the given values.
     *
     * @param context The current <code>Context</code>.
     * @param resultSet The originating <code>ResultSet</code>.
     */
    public ProjectCollection(Context context, ResultSet resultSet) {
        super(context, resultSet);
    }

    @Override
    public Project getByPrimaryKey(Object primaryKey) throws DeveloperException {
        int projectIndex;
        try {
            Collections.sort(this);
            projectIndex = Collections.binarySearch(this, new Project((Integer) primaryKey));
            if (projectIndex >= 0) {
                return super.get(projectIndex);
            } else {
                return null;
            }
        } catch (ClassCastException ccex) {
            throw new DeveloperException("Developer haven't provided correct comparing and equaling methods for Project!", ccex);
        }
    }

    @Override
    protected void acquireUniqeIdentifier(Project p) {
        p.setUniqeIdentifier(nextProjectId++);
    }
}
