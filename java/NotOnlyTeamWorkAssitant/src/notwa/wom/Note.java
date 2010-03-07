package notwa.wom;

public class Note extends BusinessObject implements Comparable<Note>,Cloneable {

	private int nID;
	private WorkItem nWorkItem;
	private String nNoteText;
	private User nAuthor;
	
	public Note (int noteID) {
		this.nID = noteID;
	}

	protected Object clone() throws CloneNotSupportedException {
		Note clone=(Note)this.clone();

		clone.nID=nID;
		clone.nWorkItem=(WorkItem)nWorkItem.clone();
		clone.nNoteText=nNoteText;
		clone.nAuthor=(User)nAuthor.clone();
		return clone;
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
	
	@Override
	public int compareTo(Note note) {
        Integer j1 = this.nID;
        Integer j2 = note.nID;
	 
        return j1.compareTo(j2);
    }
}
