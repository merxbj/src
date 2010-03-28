package notwa.wom;

public class Note extends BusinessObject implements Comparable<Note>, Cloneable {

    private NotePrimaryKey noteId;
    private String text;
    private User author;
    
    public Note (int noteId, int workItemId) {
        super();
        this.noteId = new NotePrimaryKey(noteId, workItemId);
    }

    public Note (NotePrimaryKey npk) {
        this.noteId = npk;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Note clone = (Note) super.clone();

        clone.noteId = (NotePrimaryKey) this.noteId.clone(); // deep copy of this
        clone.text = this.text;
        clone.author = this.author;
        return clone;
    }
    
    public WorkItem getWorkItem() {
        return this.currentContext.getWorkItem(noteId.workItemId);
    }
    
    public String getNoteText() {
        return this.text;
    }

    public NotePrimaryKey getId() {
        return noteId;
    }

    public String getText() {
        return text;
    }
    
    public User getAuthor() {
        return this.author;
    }
    
    public void setNoteText(String noteText) {
        this.text = noteText;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    @Override
    public String toString() {
        String returnText = new String(    this.noteId +separator );
        if(this.getWorkItem() != null) {
            returnText += this.getWorkItem().getSubject() +separator; }

            returnText += this.text +separator;
            
        if(this.author != null) {
            returnText += this.author.getLogin(); }
        return returnText;
    }
    
    @Override
    public int compareTo(Note note) {
        return this.noteId.compareTo(note.noteId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Note)) {
            return false;
        } else {
            return (this.compareTo((Note) o) == 0);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.noteId != null ? this.noteId.hashCode() : 0);
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerNote(this);
    }
}
