/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

import java.util.*;

/**
 *
 * @author eTeR
 */
public class Journal implements Comparable<Journal>, Iterable {

    public Journal(int journalId) {
        init(journalId);
    }

    public Journal() {
        init(0);
    }

    private void init(int journalId) {
        suspendGenerator = false;
        entries = new TreeSet<JournalEntry>();
        this.journalId = journalId;
        clear();

        generator = IdentifierGenerator.getInstance();
        generator.initJournalEntryIdGenerator(entries, journalId);
    }

    public int compareTo(Journal o) {
        Integer j1 = getJournalId();
        Integer j2 = o.getJournalId();

        return j1.compareTo(j2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Journal other = (Journal) obj;
        if (this.journalId != other.journalId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.journalId;
        return hash;
    }

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    public void clear()
    {
        for (JournalEntry e : entries) {
            e.detach();
        }

        entries.clear();
    }

    public void add(JournalEntry e) {
        if (!suspendGenerator) {
            int journalEntryId = generator.getNextJournalEntryId(journalId);
            e.setEntryId(journalEntryId);
        }
        
        entries.add(e);
    }

    public Iterator iterator() {
        return entries.iterator();
    }

    public void suspendGenerator() {
        suspendGenerator = true;
    }

    public void resumeGenerator() {
        suspendGenerator = false;
        generator.initJournalEntryIdGenerator(entries, journalId);
    }

    @Override
    public String toString() {
        return String.format("Journal with id %d", journalId);
    }

    private int journalId;
    private TreeSet<JournalEntry> entries;
    private IdentifierGenerator generator;
    private boolean suspendGenerator;
}
