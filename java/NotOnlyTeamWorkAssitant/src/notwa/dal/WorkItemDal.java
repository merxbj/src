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
import java.util.Hashtable;



public class WorkItemDal extends DataAccessLayer implements Fillable<WorkItemCollection>, Getable<WorkItem> {

    private Fillable<UserCollection> userDal;
    private Fillable<NoteCollection> noteDal;
    private Fillable<ProjectCollection> projectDal;
    private ProjectUserAssignmentDal puaDal;
    private NoteCollection nc;
    private Hashtable<Integer, User> users;
    private Hashtable<Integer, Project> projects;
    
    public WorkItemDal(ConnectionInfo ci) {
        super(ci);
        this.userDal = new UserDal(ci);
        this.noteDal = new NoteDal(ci);
        this.projectDal = new ProjectDal(ci);
        this.nc = new NoteCollection();
        this.puaDal = new ProjectUserAssignmentDal(ci);
    }

    @Override
    public int Fill(WorkItemCollection wic) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(wic, emptyPc);
    }
    
    @Override
    public int Fill(WorkItemCollection wic, ParameterCollection pc) {
        UserCollection ucol = new UserCollection();
        userDal.Fill(ucol);
        ProjectCollection pcol = new ProjectCollection();
        projectDal.Fill(pcol);

        puaDal.link(ucol, pcol);

        for (User u : ucol) {
            users.put(u.getId(), u);
        }

        for (Project p : pcol) {
            projects.put(p.getId(), p);
        }

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
                wi.setExpectedTimestamp(rs.getDate("expected_timestamp"));
                wi.setLastModifiedTimestamp(rs.getDate("last_modified_timestamp"));
                wi.setAssignedUser(users.get(rs.getInt("user_id")));
                wi.setProject(projects.get(rs.getInt("project_id")));
                wi.setNoteCollection(nc);
                if (!wic.add(wi)) {
                    LoggingInterface.getLogger().logWarning("Work Item (work_item_id = %d) could not be added to the collection!", wi.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return wic.size();
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
