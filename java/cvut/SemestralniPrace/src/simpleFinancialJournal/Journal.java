package simpleFinancialJournal;

import java.util.*;

/**
 * Class, representing one journal that may contain a collection of entries,
 * providing basic mehotds to operate with it (add, sort, ...)
 */
public class Journal implements Comparable<Journal>, Iterable<JournalEntry> {

    /**
     * Constructor provided with desired journal id
     * @param journalId journal id
     */
    public Journal(int journalId) {
        init(journalId);
    }

    /**
     * Basic constructor, initialising the journal with journal id of value 0
     */
    public Journal() {
        init(0);
    }

    /**
     * Class initialization
     * @param journalId id of the journal
     */
    private void init(int journalId) {
        suspend = false;
        entries = new ArrayList<JournalEntry>();
        this.journalId = journalId;
        sortColumn = new SortColumn(SortColumn.Column.ID);
        sortOrder = new SortOrder(SortOrder.Order.ASC);
        clear();

        generator = IdentifierGenerator.getInstance();
        generator.initJournalEntryIdGenerator(entries, journalId);
    }

    /**
     * Comparable interface implementation
     * @param o Journal to be compared with
     * @return -1,0,1 based on the result
     */
    public int compareTo(Journal o) {
        Integer j1 = getJournalId();
        Integer j2 = o.getJournalId();

        return j1.compareTo(j2);
    }

    /**
     * Journals are the same when their journal ids are equal
     * @param obj Journal to be compared with
     * @return true if journal ids of both journals are the same
     */
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

    public void clear() {
        for (JournalEntry e : entries) {
            e.detach();
        }

        entries.clear();
    }

    public void add(JournalEntry e) {
        if (!suspend) {
            int journalEntryId = generator.getNextJournalEntryId(journalId);
            e.entryId = journalEntryId;
        }

        if (!entries.contains(e))
            entries.add(e);

        if (!suspend) {
            sort();
        }
    }

    public void update(JournalEntry e) throws Exception {
        if (!entries.contains(e)) {
            throw new JournalException("Invalid record id to update!");
        } else {
            remove(e);
            entries.add(e);
        }
    }

    public void remove(JournalEntry e) {
        if (entries.contains(e))
            entries.remove(e);
    }

    public Iterator iterator() {
        return entries.iterator();
    }

    public void suspend() {
        suspend = true;
    }

    public void resume() {
        suspend = false;
        generator.initJournalEntryIdGenerator(entries, journalId);
        sort();
    }

    public void sort() {
        switch (getSortColumn().getColumn()) {
            case ID:
                Collections.sort(entries, new IdComparator(sortOrder));
                break;
            case AMOUNT:
                Collections.sort(entries, new AmountComparator(sortOrder));
                break;
            case DESCRIPTION:
                Collections.sort(entries, new DescriptionComparator(sortOrder));
                break;
        }
    }

    public SortColumn getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(SortColumn sortColumn) {
        this.sortColumn = sortColumn;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return String.format("Journal with id %d", journalId);
    }
    private int journalId;
    private ArrayList<JournalEntry> entries;
    private IdentifierGenerator generator;
    private boolean suspend;
    private SortColumn sortColumn;
    private SortOrder sortOrder;
}
