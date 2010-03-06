package notwa.wom;

public class Note {

	private int nID;
	private int nWorkItemID;
	private String nNote;
	private int nAuthorID;
	
	public Note (Object[] note) {
		parseDataRow(note); //TODO: add check here
	}
	
	private void parseDataRow(Object[] note) {
		this.nID = ((Integer) note[0]).intValue();
		this.nWorkItemID = ((Integer) note[1]).intValue();
		this.nNote = note[2].toString();
		this.nAuthorID = ((Integer) note[3]).intValue();;
		
	}

	public int getNoteID() {
		return this.nID;
	}
	
	public int getWorkItemID() {
		return this.nWorkItemID;
	}
	
	public String getNoteText() {
		return this.nNote;
	}
	
	public User getAutor() {
		return UserCollection.getUserByID(this.nAuthorID);
	}
}
