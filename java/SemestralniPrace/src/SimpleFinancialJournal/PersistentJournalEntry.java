/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SimpleFinancialJournal;

import java.io.*;

/**
 *
 * @author eTeR
 */
public class PersistentJournalEntry implements Serializable {

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
    
    private int journalId;
    private int entryId;
    private Money amount;
    private String description;
}
