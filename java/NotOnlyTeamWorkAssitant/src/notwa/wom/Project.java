package notwa.wom;

public class Project extends BusinessObject implements Comparable<Project>,Cloneable{
    private int pID;
	private String pName;
	private UserCollection assignedUsers;
	
	public Project (int projectID) {
		this.pID = projectID;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Project clone = (Project) super.clone();

		clone.pID = this.pID;
		clone.pName = this.pName;
		clone.assignedUsers = this.assignedUsers;
		return clone;
	}
	
	public String getProjectName() {
		return this.pName;
	}
	
	public UserCollection getAssignedUsers() {
		return assignedUsers;
	}
	
	public void setProjectName(String projectName) {
		this.pName = projectName;
	}
	
	public void setAssignedUsers(UserCollection assignedUsers) {
		this.assignedUsers = assignedUsers;
	}
	
	@Override
	public int compareTo(Project project) {
        Integer j1 = this.pID;
        Integer j2 = project.pID;
	 
        return j1.compareTo(j2);
    }
}
