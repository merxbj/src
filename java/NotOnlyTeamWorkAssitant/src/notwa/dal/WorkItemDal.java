package notwa.dal;

import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.wom.*;
import notwa.exception.DalException;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

import notwa.sql.SqlBuilder;
import java.sql.ResultSet;

public class WorkItemDal extends DataAccessLayer<WorkItem, WorkItemCollection> {
    
    public WorkItemDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    public int Fill(WorkItemCollection wic) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(wic, emptyPc);
    }
    
    @Override
    public int Fill(WorkItemCollection wic, ParameterSet pc) {
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
        
        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillWorkItemCollection(wic, sb.compileSql());
    }
    
    private int FillWorkItemCollection(WorkItemCollection wic, String sql) {
        try {
            ResultSet rs = getConnection().executeQuery(sql);

            /*
             * Open the collection and make sure that it is aware of its original
             * ResultSet!
             */
            wic.setResultSet(rs);
            wic.setClosed(false);

            while (rs.next()) {
                WorkItem wi = null;
                int workItemId = rs.getInt("work_item_id");
                if (currentContext.hasWorkItem(workItemId)) {
                    wi = currentContext.getWorkItem(workItemId);
                } else {
                    NoteDal noteDal = new NoteDal(ci, currentContext);
                    NoteCollection nc = new NoteCollection(currentContext);
                    noteDal.Fill(nc, new ParameterSet(new Parameter(Parameters.Note.WORK_ITEM_ID, workItemId, Sql.Condition.EQUALTY)));

                    ProjectDal projectDal = new ProjectDal(ci, currentContext);
                    Project project = projectDal.get(new ParameterSet(new Parameter(Parameters.Project.ID, rs.getInt("project_id"), Sql.Condition.EQUALTY)));

                    UserDal userDal = new UserDal(ci, currentContext);
                    User user = userDal.get(new ParameterSet(new Parameter(Parameters.User.ID, rs.getInt("assigned_user_id"), Sql.Condition.EQUALTY)));

                    wi = new WorkItem(workItemId);
                    wi.registerWithContext(currentContext);
                    wi.setSubject(rs.getString("subject"));
                    wi.setDescription(rs.getString("description"));
                    wi.setPriority(WorkItemPriority.lookup(rs.getInt("working_priority")));
                    wi.setStatus(WorkItemStatus.lookup(rs.getInt("status_id")));
                    wi.setExpectedTimestamp(rs.getTimestamp("expected_timestamp"));
                    wi.setLastModifiedTimestamp(rs.getTimestamp("last_modified_timestamp"));
                    wi.setAssignedUser(user);
                    wi.setProject(project);
                    wi.setParentWorkItem(get(new ParameterSet(new Parameter(Parameters.WorkItem.ID, rs.getInt("parent_work_item_id"), Sql.Condition.EQUALTY))));
                    wi.setNoteCollection(nc);
                }

                if (!wic.add(wi)) {
                    LoggingInterface.getLogger().logWarning("Work Item (work_item_id = %d) could not be added to the collection!", wi.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        } finally {
            /*
             * Make sure that the collection knows that it is up-to-date and close
             * it. This will ensure that any further addition/removal will be properly
             * remarked!
             */
            wic.setUpdateRequired(false);
            wic.setClosed(true);
        }
        return wic.size();
    }

    @Override
    public WorkItem get(ParameterSet primaryKey) throws DalException {
        Parameter p = primaryKey.first();
        if (p.getName().equals(Parameters.WorkItem.ID)) {
            int workItemId = (Integer) p.getValue();
            if (currentContext.hasWorkItem(workItemId)) {
                return currentContext.getWorkItem(workItemId);
            } else {
                WorkItemCollection wic = new WorkItemCollection(currentContext);
                int rows = this.Fill(wic, primaryKey);
                if (rows == 1) {
                    return wic.get(0);
                } else if (rows == 0) {
                    return null;
                }
            }
        }

        throw new DalException("Supplied parameters are not a primary key!");
    }
}
