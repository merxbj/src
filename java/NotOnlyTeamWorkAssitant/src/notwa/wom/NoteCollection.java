package notwa.wom;

public class NoteCollection extends BusinessObjectCollection<Note> {

    public NoteCollection() {
    }

    public NoteCollection(Context context) {
        this.currentContext = context;
    }
    
}
