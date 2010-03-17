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

    private Getable<User> userDal;
    private Fillable<NoteCollection> noteDal;
    private Getable<Project> projectDal;
    private NoteCollection nc;
    
    public WorkItemDal(ConnectionInfo ci) {
        super(ci);
        this.userDal = new UserDal(ci);
        this.noteDal = new NoteDal(ci);
        this.projectDal = new ProjectDal(ci);
        this.nc = new NoteCollection();
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
                nc.setCurrentContext(wic.getCurrentContext());
                noteDal.Fill(nc, new ParameterCollection(new Parameter[] {new Parameter(Parameters.Note.WORK_ITEM_ID, wi.getId(), Sql.Condition.EQUALTY)}));
                wi.setSubject(rs.getString("subject"));
                wi.setDescription(rs.getString("description"));
                wi.setPriority(WorkItemPriority.lookup(rs.getInt("working_priority")));
                wi.setStatus(WorkItemStatus.lookup(rs.getInt("status_id")));
                wi.setExpectedTimestamp(rs.getDate("expected_timestamp"));
                wi.setLastModifiedTimestamp(rs.getDate("last_modified_timestamp"));
                wi.setAssignedUser(getContextualUser(rs.getInt("user_id"), wic.getCurrentContext()));
                wi.setProject(getContextualProject(rs.getInt("project_id"), wic.getCurrentContext()));
                wi.setNoteCollection(nc);
                wi.registerWithContext(wic.getCurrentContext());

                if (!wic.add(wi)) {
                    LoggingInterface.getLogger().logWarning("Work Item (work_item_id = %d) could not be added to the collection!", wi.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return wic.size();
    }

    private User getContextualUser(int userId, Context context) throws DalException {
        if (context.hasUser(userId)) {
            return context.getUser(userId);
        } else {
            User user = userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)}));
            user.registerWithContext(context);
            return user;
        }
    }

    private Project getContextualProject(int projectId, Context context) throws DalException {
        if (context.hasProject(projectId)) {
            return context.getProject(projectId);
        } else {
            Project project = projectDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.Project.ID, projectId, Sql.Condition.EQUALTY)}));
            project.registerWithContext(context);
            return project;
        }
    }

    @Override
    public WorkItem get(ParameterCollection primaryKey) throws DalException {
        WorkItemCollection wic = new WorkItemCollection();
        int rows = this.Fill(wic, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        }
        return wic.get(0);
    }
}
