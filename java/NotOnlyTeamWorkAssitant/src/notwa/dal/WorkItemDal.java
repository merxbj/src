/*
 * WorkItemDal
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

import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.wom.*;
import notwa.exception.DalException;
import notwa.common.ConnectionInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * <code>WorkItemDal</code> is a <code>DataAccessLayer</code> concrete implementation
 * providing the actual data and methods how to maintain the work item data persisted
 * in the database.
 * <p>The actuall workflow is maintained by the base class itself.</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WorkItemDal extends DataAccessLayer<WorkItem, WorkItemCollection> {
    
    public WorkItemDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    protected String getSqlTemplate() {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   work_item_id, ");
        vanillaSql.append("         assigned_user_id, ");
        vanillaSql.append("         status_id, ");
        vanillaSql.append("         project_id, ");
        vanillaSql.append("         parent_work_item_id, ");
        vanillaSql.append("         subject, ");
        vanillaSql.append("         working_priority, ");
        vanillaSql.append("         description, ");
        vanillaSql.append("         expected_timestamp, ");
        vanillaSql.append("         last_modified_timestamp ");
        vanillaSql.append("FROM Work_Item ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=work_item_id;parameter=WorkItemId;}");
        vanillaSql.append("        {column=state_id;parameter=WorkItemStatusId;}");
        vanillaSql.append("        {column=working_priority;parameter=WorkItemPriorityId;}");
        vanillaSql.append("        {column=assigned_user_id;parameter=WorkItemAssignedUserId;}");
        vanillaSql.append("        {column=expected_timestamp;parameter=WorkItemDeadline;}");
        vanillaSql.append("**/");

        return vanillaSql.toString();
    }

    @Override
    protected Object getPrimaryKey(ResultSet rs) throws DalException {
        try {
            return rs.getInt("work_item_id");
        } catch (SQLException sex) {
            throw new DalException("Unable to read the work item id from the database!", sex);
        }
    }

    @Override
    protected ParameterSet getPrimaryKeyParams(Object primaryKey) {
        return new ParameterSet(new Parameter(Parameters.WorkItem.ID, primaryKey, Sql.Condition.EQUALTY));
    }

    @Override
    protected boolean isInCurrentContext(Object primaryKey) throws DalException {
        try {
            return currentContext.hasWorkItem((Integer) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected WorkItem getBusinessObject(Object primaryKey) throws DalException {
        try {
            return currentContext.getWorkItem((Integer) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected WorkItem getBusinessObject(Object primaryKey, ResultSet rs) throws DalException {
        try {
            int workItemId = (Integer) primaryKey;

            NoteDal noteDal = new NoteDal(ci, currentContext);
            NoteCollection nc = new NoteCollection(currentContext);
            noteDal.fill(nc, new ParameterSet(new Parameter(Parameters.Note.WORK_ITEM_ID, workItemId, Sql.Condition.EQUALTY)));

            ProjectDal projectDal = new ProjectDal(ci, currentContext);
            Project project = projectDal.get(rs.getInt("project_id"));

            UserDal userDal = new UserDal(ci, currentContext);
            User user = userDal.get(rs.getInt("assigned_user_id"));

            WorkItem wi = new WorkItem(workItemId);
            wi.registerWithContext(currentContext);
            wi.setSubject(rs.getString("subject"));
            wi.setDescription(rs.getString("description"));
            wi.setPriority(WorkItemPriority.lookup(rs.getInt("working_priority")));
            wi.setStatus(WorkItemStatus.lookup(rs.getInt("status_id")));
            wi.setExpectedTimestamp(rs.getTimestamp("expected_timestamp"));
            wi.setLastModifiedTimestamp(rs.getTimestamp("last_modified_timestamp"));
            wi.setAssignedUser(user);
            wi.setProject(project);
            wi.setParentWorkItem(get(rs.getInt("parent_work_item_id")));
            wi.setNoteCollection(nc);

            return wi;
        } catch (Exception ex) {
            throw new DalException("Error while parsing the WorkItem from ResultSet!", ex);
        }
    }

    @Override
    protected void updateSingleRow(ResultSet rs, WorkItem wi) throws Exception {
        Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());

        rs.updateInt("work_item_id", wi.getId());
        rs.updateString("subject", wi.getSubject());
        rs.updateString("description", wi.getDescription());
        rs.updateInt("working_priority", wi.getPriority().getValue());
        rs.updateInt("status_id", wi.getStatus().getValue());
        rs.updateTimestamp("expected_timestamp", (wi.getExpectedTimestamp() != null) ? new Timestamp(wi.getExpectedTimestamp().getTime()) : null);
        rs.updateTimestamp("last_modified_timestamp", now);
        rs.updateInt("assigned_user_id", (wi.getAssignedUser() != null) ? wi.getAssignedUser().getId() : 0);
        rs.updateInt("project_id", (wi.getProject() != null) ? wi.getProject().getId() : 0);
        rs.updateInt("parent_work_item_id", (wi.getParent() != null) ? wi.getParent().getId() : 0);

        wi.setLastModifiedTimestamp(now);
    }

    @Override
    protected String getHighestUniqeIdentifierSql(WorkItem wi) {
        return "SELECT work_item_id FROM Work_Item ORDER BY work_item_id DESC";
    }

    
}
