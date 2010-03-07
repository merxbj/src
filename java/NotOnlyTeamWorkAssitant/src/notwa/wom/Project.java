package notwa.wom;

public class Project extends BusinessObject implements Comparable<Project>,Cloneable{
    private int pID;
	private String pName;
	
	public Project (int projectID) {
		this.pID = projectID;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Project clone=(Project)this.clone();

		clone.pID=pID;
		clone.pName=pName;
		return clone;
	}
	
	public String getProjectName() {
		return this.pName;
	}
	
	public void setProjectName(String projectName) {
		this.pName = projectName;
	}
	
	@Override
	public int compareTo(Project project) {
        Integer j1 = this.pID;
        Integer j2 = project.pID;
	 
        return j1.compareTo(j2);
    }
}
