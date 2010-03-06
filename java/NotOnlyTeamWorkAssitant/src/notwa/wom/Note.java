package notwa.wom;

public class Note extends BusinessObject {

	private int nID;
	private WorkItem nWorkItem;
	private String nNoteText;
	private User nAuthor;
	
	public Note (int noteID) {
		this.nID = noteID;
	}

	public int getNoteID() {
		return this.nID;
	}
	
	public WorkItem getWorkItem() {
		return this.nWorkItem;
	}
	
	public String getNoteText() {
		return this.nNoteText;
	}
	
	public User getAuthor() {
		return this.nAuthor;
	}
	
	public void setWorkItem(WorkItem wi) {
		this.nWorkItem = wi;
	}
	
	public void setNoteText(String noteText) {
		this.nNoteText = noteText;
	}
	
	public void setAuthor(User author) {
		this.nAuthor = author;
	}
}
