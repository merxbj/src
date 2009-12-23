/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

import java.io.*;

/**
 *
 * @author eTeR
 */
public class PersistentJournalEntry implements Serializable, Comparable<PersistentJournalEntry> {

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

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersistentJournalEntry other = (PersistentJournalEntry) obj;
        if (this.journalId != other.journalId) {
            return false;
        }
        if (this.entryId != other.entryId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.journalId;
        hash = 37 * hash + this.entryId;
        return hash;
    }

    public int compareTo(PersistentJournalEntry o) {
        Integer pj1 = getJournalId();
        Integer pj2 = o.getJournalId();

        if (pj1.equals(pj2)) {
            Integer pje1 = getEntryId();
            Integer pje2 = o.getEntryId();
            
            return pje1.compareTo(pje2);
        } else {
            return pj1.compareTo(pj2);
        }
    }

    private int journalId;
    private int entryId;
    private Money amount;
    private String description;
}
