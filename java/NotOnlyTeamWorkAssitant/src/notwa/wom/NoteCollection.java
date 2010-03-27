package notwa.wom;

public class NoteCollection extends BusinessObjectCollection<Note> {

    public NoteCollection() {
    }

    public NoteCollection(Context context) {
        this.currentContext = context;
    }
    
    public Note getLatestNote() {
        return this.collection.get(this.collection.size()-1); // we must provide some sorting, to 
    }
}
