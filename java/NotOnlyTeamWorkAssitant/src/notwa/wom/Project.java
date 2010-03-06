package notwa.wom;

public class Project {
    private int pID;
	private String pName;
	
	public Project (Object[] project) {
		parseDataRow(project); //TODO: add check here
	}
	
	private void parseDataRow(Object[] project) {
		this.pID = ((Integer) project[0]).intValue();
		this.pName = project[1].toString();
	}

	public int getProjectID() {
		return this.pID;
	}
	
	public String getName() {
		return this.pName;
	}
}
