package notwa.wom;

public class Project extends BusinessObject{
    private int pID;
	private String pName;
	
	public Project (int projectID) {
		this.pID = projectID;
	}

	public int getProjectID() {
		return this.pID;
	}
	
	public String getProjectName() {
		return this.pName;
	}
	
	public void setProjectName(String projectName) {
		this.pName = projectName;
	}
}
