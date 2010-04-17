/*
 * ContextManager
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.wom;

/**
 * <code>Note</code> represents a single note taken within its parrent {@link WorkItem}>.
 * Every <code>Note</code> has a noteId which uniqely identifies it within its
 * parrent<code>WorkItem</code> and it is sorted by a descending order to have the
 * latest <code>Note</code>s on the top of the sorted collection.
 *
 * @author Jaroslav Merxbauer
 * @author Tomas Studnicka
 */
public class Note extends BusinessObject implements Comparable<Note>, Cloneable {

    private NotePrimaryKey noteId;
    private String text;
    private User author;

    /**
     * The constructor which identifies this <code>Note</code> with the parent
     * <code>WorkItem</code> only and waiting to acquire the noteId during the
     * addition to the <code>BusinessObjectCollection</code>.
     * @param workItemId    The id of the <code>WorkItem</code> this <code>Note<code>
     *                      is bound to.
     */
    public Note(int workItemId) {
        super();
        this.noteId = new NotePrimaryKey(0, workItemId);
    }

    /**
     * The simplier contructor accepting noteId and workItemId which uniquely
     * identifies this <code>Note</code>.
     * Constructor then creates a new {@link NotePrimaryKey} based on given
     * parameters and use it as an uniqe identifier.
     *
     * @param noteId The note id which is uniqe always under one <code>WorkItem</code>.
     * @param workItemId The workItemId where this <code>Note</code> is valid.
     */
    public Note(int noteId, int workItemId) {
        super();
        this.noteId = new NotePrimaryKey(noteId, workItemId);
    }

    /**
     * The contructor accepting already existing <code>NotePrimaryKey</code> which
     * uniquely identifies this <code>Note</code>.
     *
     * @param npk   The <code>NotePrimaryKey</code> which consist from noteId and
     *              workItemId.
     */
    public Note (NotePrimaryKey npk) {
        this.noteId = npk;
    }

    /**
     * Creates a shallow copy of this <code>Note</code>.
     *
     * @return New <code>Note</code> cloned from this <code>Note</code>.
     * @throws CloneNotSupportedException When cloning error occures.
     */
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
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setAuthor(User author) {
        this.author = author;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
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

    @Override
    public boolean hasUniqeIdentifier() {
        return (this.noteId.getNoteId() > 0);
    }

    @Override
    public void setUniqeIdentifier(int value) {
        this.getId().setNoteId(value);
    }

    @Override
    public int getUniqeIdentifier() {
        return getId().getNoteId();
    }
}
