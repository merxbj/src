package notwa.wom;

public class ProjectUserAssigment extends BusinessObject implements Comparable<ProjectUserAssigment>,Cloneable {

	private Project pProject;
	private User pUser;

	protected Object clone() throws CloneNotSupportedException {
		ProjectUserAssigment clone=(ProjectUserAssigment)this.clone();

		clone.pProject=(Project)pProject.clone();
		clone.pUser=(User)pUser.clone();
		return clone;
	}
	
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
