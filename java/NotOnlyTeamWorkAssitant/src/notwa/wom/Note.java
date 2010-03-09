package notwa.wom;

public class Note extends BusinessObject implements Comparable<Note>, Cloneable {

	private int noteID;
	private WorkItem workItem;
	private String text;
	private User author;
	
	public Note (int noteID) {
		this.noteID = noteID;
	}

	protected Object clone() throws CloneNotSupportedException {
		Note clone = (Note) super.clone();

		clone.noteID = this.noteID;
		clone.workItem = this.workItem;
		clone.text = this.text;
		clone.author = this.author;
		return clone;
	}
	
	public WorkItem getWorkItem() {
		return this.workItem;
	}
	
	public String getNoteText() {
		return this.text;
	}
	
	public User getAuthor() {
		return this.author;
	}
	
	public void setWorkItem(WorkItem wi) {
		this.workItem = wi;
	}
	
	public void setNoteText(String noteText) {
		this.text = noteText;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	
	@Override
	public String toString() {
		String returnText = new String(	this.noteID +separator );
		if(this.workItem != null) {
			returnText += this.workItem.getSubject() +separator; }

			returnText += this.text +separator;
			
		if(this.author != null) {
			returnText += this.author.getLoginName(); }
		return returnText;
	}
	
	@Override
	public int compareTo(Note note) {
        Integer j1 = this.noteID;
        Integer j2 = note.noteID;
	 
        return j1.compareTo(j2);
    }
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Note)) {
			return false;
		} else {
			return (this.compareTo((Note) o) == 0);
		}
	}
}
