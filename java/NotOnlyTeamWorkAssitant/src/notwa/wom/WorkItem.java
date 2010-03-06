package notwa.wom;

import java.util.Date;

public class WorkItem extends BusinessObject {

	private int wiID;
	private String wiSubject;
	private WorkItemPriority wiPriority;
	private String wiDescription;
	private User wiAssignedUser;
	private WorkItemState wiState;
	private Date wiLastModified;
	private Project wiProject;
	private WorkItem wiParentWorkItem;
	private Date wiDeadLineDate;

	public WorkItem(int wiID) {
		this.wiID = wiID;
	}

	public int getID() {
		return this.wiID;
	}

	public String getSubject() {
		return this.wiSubject;
	}

	public WorkItemPriority getPriority() {
		return this.wiPriority;
	}

	public String getDescription() {
		return this.wiDescription;
	}

	public WorkItemState getState() {
		return this.wiState;
	}

	public Date getLastModified() {
		return this.wiLastModified;
	}

	public Project getProject() {
		return this.wiProject;
	}

	public WorkItem getParent() {
		return this.wiParentWorkItem;
	}

	public Date getDeadLineDate() {
		return this.wiDeadLineDate;
	}
	
	public User getAssignedUser() {
		return wiAssignedUser;
	}

	public void setWiSubject(String wiSubject) {
		this.wiSubject = wiSubject;
	}

	public void setWiPriority(WorkItemPriority wiPriority) {
		this.wiPriority = wiPriority;
	}

	public void setWiDescription(String wiDescription) {
		this.wiDescription = wiDescription;
	}

	public void setWiAssignedUser(User wiAssignedUser) {
		this.wiAssignedUser = wiAssignedUser;
	}

	public void setWiState(WorkItemState wiState) {
		this.wiState = wiState;
	}

	public void setWiLastModified(Date wiLastModified) {
		this.wiLastModified = wiLastModified;
	}

	public void setWiProject(Project wiProject) {
		this.wiProject = wiProject;
	}

	public void setWiParentWorkItem(WorkItem wiParentWorkItem) {
		this.wiParentWorkItem = wiParentWorkItem;
	}

	public void setWiDeadLineDate(Date wiDeadLineDate) {
		this.wiDeadLineDate = wiDeadLineDate;
	}
}