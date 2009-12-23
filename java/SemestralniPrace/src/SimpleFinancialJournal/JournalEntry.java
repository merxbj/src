/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class JournalEntry implements Comparable<JournalEntry>{

    public int compareTo(JournalEntry o) {
        Integer e1 = getEntryId();
        Integer e2 = o.getEntryId();
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
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

    private int entryId;
    private Money amount;
    private String description;
    private Journal journal;

}
