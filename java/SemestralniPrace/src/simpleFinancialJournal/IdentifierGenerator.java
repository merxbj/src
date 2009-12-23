package simpleFinancialJournal;

import java.util.*;

/**
 * Simple helper class that provides a method to generate ID's for Journals and
 * JournalEntries.
 * It's implemented as a singleton.
 */
public class IdentifierGenerator {

    /**
     * Singleton - return the actual instance
     */
    public static IdentifierGenerator getInstance() {
        if (instance == null) {
            instance = new IdentifierGenerator();
        }
        return instance;
    }

    /**
     * Private default constructor, setting members to its default values
     */
    private IdentifierGenerator() {
        nextJournalId = 0;
        journalToEntries = new HashMap<Integer, Integer>();
    }

    /**
     * Determines next valid journal entry id, based upon given list of entries
     */
    public void initJournalEntryIdGenerator(ArrayList<JournalEntry> entries, int journalId) {
        Collections.sort(entries);
        if (entries.size() == 0) {
            journalToEntries.put(journalId, 1);
        } else {
            journalToEntries.put(journalId, entries.get(entries.size()-1).getEntryId() + 1);
        }
    }

    /**
     * Determines next valid journal id, based upon given list of jorunals
     */
    public void initJournalIdGenerator(TreeSet<Journal> journals) {
        if (journals.size() == 0) {
            nextJournalId = 1;
        } else {
            nextJournalId = journals.last().getJournalId() + 1;
        }
    }

    /**
     * Returns the next valid journal entry id.
     * Because we want to number the entries for each possible journal separately,
     * we are storing it to the hash map (mapped by journal id)
     */
    public int getNextJournalEntryId(int journalId) {
        int nextJournalEntryId = journalToEntries.get(journalId);
        journalToEntries.put(journalId, nextJournalEntryId + 1);

        return nextJournalEntryId;
    }

    /**
     * Returns the next valid journal id.
     */
    public int getNextJournalId() {
        return nextJournalId++;
    }
    private int nextJournalId;
    private static IdentifierGenerator instance;
    private HashMap<Integer, Integer> journalToEntries;
}
