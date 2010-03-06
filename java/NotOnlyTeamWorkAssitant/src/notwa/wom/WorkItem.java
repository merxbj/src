package notwa.wom;

import java.util.Date;

public class WorkItem{
	
	private int wiID;
	private String wiSubject;
	private int wiPriority;
	private String wiDescription;
	private int wiCurrentLeaderID;
	private int wiStateID;
	private Date wiLastModified;
	private int wiProjectID;
	private int wiParentWorkItemID;
	private Date wiDeadLineDate;
	
	public WorkItem(Object[] wi) {
		parseDataRow(wi); //TODO: add check here
	}
	//TODO:missing priority!!
	private void parseDataRow(Object[] wi) {
		this.wiID = ((Integer) wi[0]).intValue();
		this.wiSubject = wi[1].toString();
		this.wiPriority = ((Integer) wi[2]).intValue();
		this.wiDescription = wi[2].toString();
		this.wiCurrentLeaderID = ((Integer) wi[3]).intValue();
		this.wiStateID = ((Integer) wi[4]).intValue();
		this.wiLastModified = ((Date) wi[5]);
		this.wiProjectID = ((Integer) wi[6]).intValue();
		this.wiParentWorkItemID = ((Integer) wi[7]).intValue();
		this.wiDeadLineDate = ((Date) wi[5]);		
	}

	public int getID() {
		return this.wiID;
	}
	
	public String getSubject() {
		return this.wiSubject;
	}
	
	public int getPriority() {
		return this.wiPriority;
	}
	
	public String getDescription() {
		return this.wiDescription;
	}

	public int getStateID() {
		return this.wiStateID;
	}
	
	public Date getLastModified() {
		return this.wiLastModified;
	}

	public int getProjectID() {
		return this.wiProjectID;
	}

	public int getParentID() {
		return this.wiParentWorkItemID;
	}

	public Date getDeadLineDate() {
		return this.wiDeadLineDate;
	}
	public User getAuthor() {
		return UserCollection.getUserByID(this.wiCurrentLeaderID);
	}
}