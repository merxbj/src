package notwa.wom;

public class ProjectUserAssigment extends BusinessObject {

	private Project project;
	private User pUser;
	
	public ProjectUserAssigment(Project project) {
		this.project = project;
	}
	
	public Project getProject() {
		return this.project;
	}
	
	public User getAssignedUser() {
		return this.pUser;
	}
	
	public void setAssignedUser(User user) {
		this.pUser = user;
	}
}
