package notwa.wom;

public class Note extends BusinessObject implements Comparable<Note>, Cloneable {

    private NotePrimaryKey noteId;
    private String text;
    private User author;
    
    public Note (int noteId, int workItemId) {
        this.noteId = new NotePrimaryKey(noteId, workItemId);
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

    public class NotePrimaryKey implements Comparable<NotePrimaryKey>, Cloneable {
        int noteId;
        int workItemId;

        public NotePrimaryKey(int noteId, int workItemId) {
            this.noteId = noteId;
            this.workItemId = workItemId;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            NotePrimaryKey clone = (NotePrimaryKey) super.clone();
            clone.noteId = this.noteId;
            clone.workItemId = this.workItemId;
            return clone;
        }

        public int getNoteId() {
            return noteId;
        }

        public int getWorkItemId() {
            return workItemId;
        }

        @Override
        public int compareTo(NotePrimaryKey npk) {
            Integer id1 = this.noteId;
            Integer id2 = npk.noteId;
            Integer wi1 = this.workItemId;
            Integer wi2 = npk.workItemId;

            int compare = wi1.compareTo(wi2);
            if (compare == 0) {
                compare = id1.compareTo(id2);
            }

            return compare;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            
            return (((NotePrimaryKey) obj).compareTo(this) == 0);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 41 * hash + this.noteId;
            hash = 41 * hash + this.workItemId;
            return hash;
        }
    }
}
