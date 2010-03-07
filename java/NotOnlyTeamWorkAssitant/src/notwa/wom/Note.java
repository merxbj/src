package notwa.wom;

public class Note extends BusinessObject implements Comparable<Note>, Cloneable {

	private int id;
	private WorkItem workItem;
	private String text;
	private User author;
	
	public Note (int noteID) {
		this.id = noteID;
	}

	protected Object clone() throws CloneNotSupportedException {
		Note clone = (Note) super.clone();

		clone.id = this.id;
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
	public int compareTo(Note note) {
        Integer j1 = this.id;
        Integer j2 = note.id;
	 
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
