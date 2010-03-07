package notwa.wom;

public class User extends BusinessObject implements Comparable<User>,Cloneable {

	private int uID;
	private String uLogin;
	private String uPassword;
	private String uUserFirstName;
	private String uUserLastName;
	private ProjectCollection assignedProjects;
	
	public User(int userID) {
		this.uID = userID;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		User clone=(User)this.clone();

		clone.uID = this.uID;
		clone.uLogin = this.uLogin;
		clone.uPassword = this.uPassword;
		clone.uUserFirstName = this.uUserFirstName;
		clone.uUserLastName = this.uUserLastName;
		clone.assignedProjects = this.assignedProjects;
		return clone;
	}
	
	public String getLoginName() {
		return this.uLogin;
	}

	public String getLoginPass() {
		return this.uPassword;
	}
	
	public String getFirstName() {
		return this.uUserFirstName;
	}
	
	public String getLastName() {
		return this.uUserLastName;
	}
	
	public ProjectCollection getAssignedProjects() {
		return assignedProjects;
	}
	
	public void setLoginName(String loginName) {
		this.uLogin = loginName;
	}
	
	public void setUserPassword(String userPassword) {
		this.uPassword = userPassword;
	}
	
	public void setUserFirstName(String firstName) {
		this.uUserFirstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.uUserLastName = lastName;
	}
	
	public void setAssignedProjects(ProjectCollection assignedProjects) {
		this.assignedProjects = assignedProjects;
	}
	
	@Override
	public int compareTo(User user) {
        Integer j1 = this.uID;
        Integer j2 = user.uID;
	 
        return j1.compareTo(j2);
    }
}
