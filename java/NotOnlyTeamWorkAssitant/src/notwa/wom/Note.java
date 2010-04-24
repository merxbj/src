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

    private NotePrimaryKey id;
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
        this.id = new NotePrimaryKey(0, workItemId);
    }

    /**
     * The simplier contructor accepting noteId and workItemId which uniquely
     * identifies this <code>Note</code>.
     * Constructor then creates a new {@link NotePrimaryKey} based on given
     * parameters and use it as an uniqe identifier.
     *
     * @param id The note id which is uniqe always under one <code>WorkItem</code>.
     * @param workItemId The workItemId where this <code>Note</code> is valid.
     */
    public Note(int id, int workItemId) {
        super();
        this.id = new NotePrimaryKey(id, workItemId);
    }

    /**
     * The contructor accepting already existing <code>NotePrimaryKey</code> which
     * uniquely identifies this <code>Note</code>.
     *
     * @param npk   The <code>NotePrimaryKey</code> which consist from noteId and
     *              workItemId.
     */
    public Note (NotePrimaryKey npk) {
        this.id = npk;
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

        clone.id = (NotePrimaryKey) this.id.clone(); // deep copy of this
        clone.text = this.text;
        clone.author = this.author;
        return clone;
    }

    /**
     * Gets the <code>WorkItem</code> this <code>Note</code> is assigned to.
     *
     * @return The <code>WorkItem</code>
     */
    public WorkItem getWorkItem() {
        return this.currentContext.getWorkItem(id.getWorkItemId());
    }

    /**
     * Gets this <code>Note</code> primary key.
     *
     * @return The primary key.
     */
    public NotePrimaryKey getId() {
        return id;
    }

    /**
     * Gets this <code>Note</code> text.
     *
     * @return The text.
     */
    public String getText() {
        return text;
    }
    
    /**
     * Gets this <code>Note</code> author.
     *
     * @return The author.
     */
    public User getAuthor() {
        return this.author;
    }
    
    /**
     * Sets this <code>Note</code> text.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Note</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param text The new text.
     */
    public void setNoteText(String text) {
        this.text = text;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets this <code>Note</code> new author.
     *
     * <p>Please consider the consequences when changing this property when 
     * this <code>Note</code> is already a member of a closed 
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param author The new author.
     */
    public void setAuthor(User author) {
        this.author = author;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    @Override
    public String toString() {
        String returnText = String.format("%d | %s | %s | %s",
                id,
                (getWorkItem() != null) ? getWorkItem().getSubject() : "wi:null",
                text,
                (author != null) ? author.getLogin() : "u:null");

        return returnText;
    }
    
    @Override
    public int compareTo(Note note) {
        return this.id.compareTo(note.id);
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
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerNote(this);
    }

    @Override
    public boolean hasUniqeIdentifier() {
        return (this.id.getNoteId() > 0);
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
