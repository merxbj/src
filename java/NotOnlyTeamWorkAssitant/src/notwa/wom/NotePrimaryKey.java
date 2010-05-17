/*
 * NotePrimaryKey
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
 * Class representing a composite primary key of the <code>Note</code>. The primary
 * key is made from the <code>Note</code> identifier and the identifier of
 * <code>WorkItem</code> this <code>Note</code> is assigned to.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class NotePrimaryKey implements Comparable<NotePrimaryKey>, Cloneable {
    
    private int noteId;
    private int workItemId;

    /**
     * The constructor accepting only the workItemId. The expection is that someone
     * will supply the noteId later.
     * <p>This is usually done when we don't alrady know the new noteId and expecting
     * the database update to provide it with us.</p>
     *
     * @param workItemId The workItemid.
     */
    public NotePrimaryKey(int workItemId) {
        this.workItemId = workItemId;
        this.noteId = 0;
    }

    /**
     * Full feature constructor.
     *
     * @param noteId The noteId.
     * @param workItemId The workItemId.
     */
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

    /**
     * Gets the noteId. Please be advised that the return value may be 0 for newly
     * inserted notes before they get updated againts the database.
     *
     * @return The noteId.
     */
    public int getNoteId() {
        return noteId;
    }

    /**
     * Gets the workItemId.
     * 
     * @return The workItemId.
     */
    public int getWorkItemId() {
        return workItemId;
    }

    /**
     * Sets the noteId.
     * 
     * @param noteId The new noteId.
     */
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Comparable implementation sorting the notes primarily by the
     * assigned <code>WorkItem</code> and then descendingly by the note id
     * which ensures the first is the latest note.
     *
     * @param other <code>NotePrimaryKey</code> to be compared with.
     * @return Usual comparable output based on the rules explained above.
     */
    @Override
    public int compareTo(NotePrimaryKey other) {
        Integer id1 = this.noteId;
        Integer id2 = other.noteId;
        Integer wi1 = this.workItemId;
        Integer wi2 = other.workItemId;

        int compare = wi1.compareTo(wi2);
        if (compare == 0) {
            compare = id2.compareTo(id1);
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