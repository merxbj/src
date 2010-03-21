package notwa.dal;

import notwa.sql.ParameterCollection;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.wom.*;
import notwa.exception.DalException;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

import notwa.sql.SqlBuilder;
import java.sql.ResultSet;



public class WorkItemDal extends DataAccessLayer implements Fillable<WorkItemCollection>, Getable<WorkItem> {

    private Fillable<NoteCollection> noteDal;
    private NoteCollection nc;
    private ConnectionInfo ci;
    private Context currentContext;
    
    public WorkItemDal(ConnectionInfo ci, Context context) {
        super(ci);
        
        this.noteDal = new NoteDal(ci, context);
        this.nc = new NoteCollection(context);
        this.ci = ci;
        this.currentContext = context;
    }

    @Override
    public int Fill(WorkItemCollection wic) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(wic, emptyPc);
    }
    
    @Override
    public int Fill(WorkItemCollection wic, ParameterCollection pc) {

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
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                WorkItem wi = new WorkItem(rs.getInt("work_item_id"));
                noteDal.Fill(nc, new ParameterCollection(new Parameter[] {new Parameter(Parameters.Note.WORK_ITEM_ID, wi.getId(), Sql.Condition.EQUALTY)}));
                wi.setSubject(rs.getString("subject"));
                wi.setDescription(rs.getString("description"));
                wi.setPriority(WorkItemPriority.lookup(rs.getInt("working_priority")));
                wi.setStatus(WorkItemStatus.lookup(rs.getInt("status_id")));
                wi.setExpectedTimestamp(rs.getTimestamp("expected_timestamp"));
                wi.setLastModifiedTimestamp(rs.getTimestamp("last_modified_timestamp"));
                wi.setAssignedUser(getContextualUser(rs.getInt("assigned_user_id")));
                wi.setProject(getContextualProject(rs.getInt("project_id")));
                wi.setParentWorkItem(getContextualWorkItem(rs.getInt("parent_work_item_id")));
                wi.setNoteCollection(nc);
                wi.registerWithContext(currentContext);

                if (!wic.add(wi)) {
                    LoggingInterface.getLogger().logWarning("Work Item (work_item_id = %d) could not be added to the collection!", wi.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return wic.size();
    }

    private WorkItem getContextualWorkItem(int workItemId) throws DalException {
        if (currentContext.hasWorkItem(workItemId)) {
            return currentContext.getWorkItem(workItemId);
        } else {
            WorkItem wi = this.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.WorkItem.ID, workItemId, Sql.Condition.EQUALTY)}));
            return wi;
        }
    }

    private User getContextualUser(int userId) throws DalException {
        if (currentContext.hasUser(userId)) {
            return currentContext.getUser(userId);
        } else {
            Getable<User> userDal = new UserDal(ci, currentContext);
            User user = userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)}));
            return user;
        }
    }

    private Project getContextualProject(int projectId) throws DalException {
        if (currentContext.hasProject(projectId)) {
            return currentContext.getProject(projectId);
        } else {
            Getable<Project> projectDal = new ProjectDal(ci, currentContext);
            Project project = projectDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.Project.ID, projectId, Sql.Condition.EQUALTY)}));
            return project;
        }
    }

    @Override
    public WorkItem get(ParameterCollection primaryKey) throws DalException {
        WorkItemCollection wic = new WorkItemCollection(currentContext);
        int rows = this.Fill(wic, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        } else if (rows == 0) {
            return null;
        }
        return wic.get(0);
    }
}
