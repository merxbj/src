package simpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class JournalEntry implements Comparable<JournalEntry> {

    public int compareTo(JournalEntry o) {
        Integer e1 = entryId;
        Integer e2 = o.entryId;
        return e1.compareTo(e2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JournalEntry other = (JournalEntry) obj;
        if (this.entryId != other.entryId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.entryId;
        return hash;
    }

    public Journal attachedJournal() {
        return journal;
    }

    public void attach(Journal journal) {
        this.journal = journal;
    }

    public void detach() {
        this.journal = null;
    }

    @Override
    public String toString() {
        return String.format("%6d : %10s : %s", entryId, amount, description);
    }
    public int entryId;
    public Money amount;
    public String description;
    private Journal journal;
}
