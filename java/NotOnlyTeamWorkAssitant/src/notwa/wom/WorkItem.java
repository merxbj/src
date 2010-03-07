package notwa.wom;

import java.util.Date;

public class WorkItem extends BusinessObject implements Comparable<WorkItem>,Cloneable {

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
	private NoteCollection noteCollection;

	enum WorkItemState {
		PLEASE_RESOLVE,	WAITING, IN_PROGRESS, CLOSED, DELETED }

	enum WorkItemPriority {
		CRITICAL, IMPORTANT, NORMAL, NICE_TO_HAVE, UNNECESSARY }
	
	public WorkItem(int wiID) {
		this.wiID = wiID;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		WorkItem clone = (WorkItem) super.clone();

		clone.wiID = this.wiID;
		clone.wiSubject = this.wiSubject;
		clone.wiPriority = this.wiPriority;
		clone.wiDescription = this.wiDescription;
		clone.wiAssignedUser = this.wiAssignedUser;
		clone.wiState = this.wiState;
		clone.wiLastModified = this.wiLastModified;
		clone.wiProject = this.wiProject;
		clone.wiParentWorkItem = this.wiParentWorkItem;
		clone.wiDeadLineDate = this.wiDeadLineDate;
		clone.noteCollection = this.noteCollection;
		return clone;
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
	
	public NoteCollection getNoteCollection() {
		return noteCollection;
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
	public void setNoteCollection(NoteCollection noteCollection) {
		this.noteCollection = noteCollection;
	}

	@Override
	public int compareTo(WorkItem wi) {
        Integer j1 = this.wiID;
        Integer j2 = wi.wiID;
	 
        return j1.compareTo(j2);
    }
}