package notwa.wom;

public class ProjectUserAssigment extends BusinessObject implements Comparable<ProjectUserAssigment> {

	private Project pProject;
	private User pUser;
	
	public Project getProject() {
		return this.pProject;
	}
	
	public User getAssignedUser() {
		return this.pUser;
	}

	public void setProject(Project project) {
		this.pProject = project;
	}
	
	public void setAssignedUser(User user) {
		this.pUser = user;
	}
	
	@Override
	public int compareTo(ProjectUserAssigment pua) {
        String j1 = this.pProject.getProjectName();
        String j2 = pua.getProject().getProjectName();
	 
        return j1.compareTo(j2);
    }
}
