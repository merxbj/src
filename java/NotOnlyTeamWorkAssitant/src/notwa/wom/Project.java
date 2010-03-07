package notwa.wom;

public class Project extends BusinessObject implements Comparable<Project>, Cloneable {
    private int id;
	private String name;
	private UserCollection assignedUsers;
	
	public Project (int projectID) {
		this.id = projectID;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Project clone = (Project) super.clone();

		clone.id = this.id;
		clone.name = this.name;
		clone.assignedUsers = this.assignedUsers;
		return clone;
	}
	
	public String getProjectName() {
		return this.name;
	}
	
	public UserCollection getAssignedUsers() {
		return assignedUsers;
	}
	
	public void setProjectName(String projectName) {
		this.name = projectName;
	}
	
	public void setAssignedUsers(UserCollection assignedUsers) {
		this.assignedUsers = assignedUsers;
	}
	
	@Override
	public int compareTo(Project project) {
        Integer j1 = this.id;
        Integer j2 = project.id;
	 
        return j1.compareTo(j2);
    }
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Project)) {
			return false;
		} else {
			return (this.compareTo((Project) o) == 0);
		}
	}
}
