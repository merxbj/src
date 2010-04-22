/*
 * ParameterSet
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
package notwa.sql;

/**
 * Abstract class which only purpose is to hold the code definitions for the
 * <code>Parameter</code> names.
 * <p>The properties of this class should be always used for defining a <code>Parameter<code>
 * name as it definitely prevents from typos and enforces the java code editor tools
 * to help you with finding the desired <code>Parameter</code> name.</p>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Parameters {

    /**
     * Parameter names for <code>WorkItem</code>.
     */
    public static final class WorkItem {
        /**
         * Parameter name for column work_item_id.
         */
        public static final String ID                   = "WorkItemId";

        /**
         * Parameter name for column status.
         */
        public static final String STATUS               = "WorkItemStatusId";

        /**
         * Parameter name for column priority
         */
        public static final String PRIORITY             = "WorkItemPriorityId";
        
        /**
         * Parameter name for column assigned_user
         */
        public static final String ASSIGNED_USER        = "WorkItemAssignedUserId";

        /**
         * Parameter name for column expected_timestamp
         */
        public static final String DEADLINE             = "WorkItemDeadline";
    }

    /**
     * Parameter names for <code>User</code>.
     */
    public static final class User {
        
        /**
         * Parameter name for column user_id
         */
        public static final String ID                   = "UserId";

        /**
         * Parameter name for column login
         */
        public static final String LOGIN                = "UserLogin";

        /**
         * Parameter name for column first_name
         */
        public static final String FIRST_NAME           = "UserFirstName";

        /**
         * Parameter name for column last_name
         */
        public static final String LAST_NAME            = "UserLastName";
    }

    /**
     * Parameter names for <code>Project</code>.
     */
    public static final class Project {
        
        /**
         * Parameter name for column project_id
         */
        public static final String ID                   = "ProjectId";

        /**
         * Parameter name for column project_name
         */
        public static final String NAME                 = "ProjectName";
    }

    /**
     * Parameter names for <code>Note</code>
     */
    public static final class Note {
        
        /**
         * Parameter name for column note_id
         */
        public static final String ID                   = "NoteId";
        
        /**
         * Parameter name for column work_item_id
         */
        public static final String WORK_ITEM_ID         = "NoteWorkItemId";
    }
}
