package notwa.sql;

public abstract class Parameters {

    public static final class WorkItem {    
        public static final String ID                 = "WorkItemId";
        public static final String STATUS             = "WorkItemStatusId";
        public static final String PRIORITY         = "WorkItemPriorityId";
        public static final String ASSIGNED_USER     = "WorkItemAssignedUserId";
        public static final String DEADLINE         = "WorkItemDeadline";
    }

    public static final class User {    
        public static final String ID                 = "UserId";
        public static final String LOGIN             = "UserLogin";
        public static final String FIRST_NAME         = "UserFirstName";
        public static final String LAST_NAME         = "UserLastName";
    }

    public static final class Project {    
        public static final String ID                 = "ProjectId";
        public static final String NAME             = "ProjectName";
    }

    public static final class Note {    
        public static final String ID                 = "NoteId";
        public static final String WORK_ITEM_ID     = "NoteWorkItemId";
    }
}
