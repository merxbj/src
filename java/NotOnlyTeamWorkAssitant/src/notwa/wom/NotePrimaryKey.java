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

public class NotePrimaryKey implements Comparable<NotePrimaryKey>, Cloneable {
    int noteId;
    int workItemId;

    public NotePrimaryKey(int workItemId) {
        this.workItemId = workItemId;
        this.noteId = 0;
    }

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

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Comparable implementation sorting the notes primarily by the
     * assigned <code>WorkItem</code> and then descendingly by the note id
     * which ensures the first is the latest note.
     *
     * @param npk <code>NotePrimaryKey</code> to be compared with.
     * @return Usual comparable output based on the rules explained above.
     */
    @Override
    public int compareTo(NotePrimaryKey npk) {
        Integer id1 = this.noteId;
        Integer id2 = npk.noteId;
        Integer wi1 = this.workItemId;
        Integer wi2 = npk.workItemId;

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