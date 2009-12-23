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
public class IdentifierGenerator {

    public static IdentifierGenerator getInstance() {
        if (instance == null) {
            instance = new IdentifierGenerator();
        }
        return instance;
    }

    private IdentifierGenerator() {
        nextJournalId = 0;
        journalToEntries = new HashMap<Integer, Integer>();
    }

    public void initJournalEntryIdGenerator(TreeSet<JournalEntry> entries, int journalId) {
        if (entries.size() == 0)
            journalToEntries.put(journalId, 1);
        else
            journalToEntries.put(journalId, entries.last().getEntryId() + 1);
    }

    public void initJournalIdGenerator(TreeSet<Journal> journals) {
        if (journals.size() == 0)
            nextJournalId = 1;
        else
            nextJournalId = journals.last().getJournalId() + 1;
    }

    public int getNextJournalEntryId(int journalId) {
        int nextJournalEntryId = journalToEntries.get(journalId);
        journalToEntries.put(journalId, nextJournalEntryId + 1);

        return nextJournalEntryId;
    }

    public int getNextJournalId() {
        return nextJournalId++;
    }

    private int nextJournalId;
    private static IdentifierGenerator instance;
    private HashMap<Integer, Integer> journalToEntries;
}
