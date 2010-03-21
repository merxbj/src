package notwa.wom;

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