package notwa.dal;

import notwa.sql.ParameterCollection;
import notwa.wom.*;

import java.lang.StringBuilder;
import notwa.sql.SqlBuilder;
import java.sql.ResultSet;
import notwa.common.LoggingInterface;

public class WorkItemDal extends DataAccessLayer implements Fillable<WorkItemCollection>, Getable<WorkItem> {

	private Getable userDal;
	private Getable noteDal;
	private Getable projectDal;
	
	public int Fill(WorkItemCollection wic) {
		ParameterCollection emptyPc = new ParameterCollection();
		return Fill(wic, emptyPc);
	}
	
	public int Fill(WorkItemCollection wic, ParameterCollection pc) {
		StringBuilder vanillaSql = new StringBuilder();
		vanillaSql.append("SELECT 	work_item_id,");
		vanillaSql.append("       	assigned_user_id,");
		vanillaSql.append("       	state_id,");
		vanillaSql.append("       	project_id,");
		vanillaSql.append("       	parent_work_item_id,");
		vanillaSql.append("       	subject,");
		vanillaSql.append(" 		working_priority,");
		vanillaSql.append(" 		description,");
		vanillaSql.append("			expected_timestamp,");
		vanillaSql.append(" 		last_modified_timestamp");
		vanillaSql.append("FROM Work_Items");
		vanillaSql.append("/* STATEMENT=WHERE;RELATION=AND;");
		vanillaSql.append("		{column=work_item_id;parameter=WorkItemId;}");
		vanillaSql.append("		{column=state_id;parameter=WorkItemStatusId;}");
		vanillaSql.append("		{column=working_priority;parameter=WorkItemPriorityId;}");
		vanillaSql.append(" 	{column=assigned_user_id;parameter=WorkItemAssignedUserId;}");
		vanillaSql.append("		{column=expected_timestamp;parameter=WorkItemDeadline;}");
		vanillaSql.append("*/");
		
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
				wi.setPriority((WorkItem.WorkItemPriority) rs.getObject("working_priority"));
				wi.
			}
		} catch (Exception ex) {
			LoggingInterface.getInstanece().handleException(ex);
		}
		return 1;
	}

	public void get(WorkItem boc, ParameterCollection primaryKey) {
		
	}
}
