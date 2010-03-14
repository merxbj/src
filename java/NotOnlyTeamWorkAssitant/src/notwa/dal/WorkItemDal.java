package notwa.dal;

import notwa.sql.ParameterCollection;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.wom.*;

import notwa.sql.SqlBuilder;
import java.sql.ResultSet;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

public class WorkItemDal extends DataAccessLayer implements Fillable<WorkItemCollection>, Getable<WorkItem> {

    private Getable<User> userDal;
    private Getable<Note> noteDal;
    private Getable<Project> projectDal;
    
    public WorkItemDal(ConnectionInfo ci) {
        super(ci);
        this.userDal = new UserDal(ci);
        this.noteDal = new NoteDal(ci);
        this.projectDal = new ProjectDal(ci);
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
                wi.setSubject(rs.getString("subject"));
                wi.setDescription(rs.getString("description"));
                wi.setPriority(WorkItemPriority.lookup(rs.getInt("working_priority")));
                wi.setStatus(WorkItemStatus.lookup(rs.getInt("status_id")));
                wi.setExpectedTimestamp(rs.getDate("expected_timestamp"));
                wi.setLastModifiedTimestamp(rs.getDate("last_modified_timestamp"));
                wi.setAssignedUser(userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, rs.getInt("assigned_user_id"), Sql.Condition.EQUALTY)})));
                //wi.setAssignedUser(userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, rs.getInt("assigned_user_id"), Sql.Condition.EQUALTY)})));
                // TODO Complete!
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return 1;
    }

    @Override
    public WorkItem get(ParameterCollection primaryKey) {
        // TODO Auto-generated method stub        
        return new WorkItem(1);
    }
}
