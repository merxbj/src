package simpleFinancialJournal;

import java.util.*;

/*
 * Provides comparator implementation for sorting an array of JournalEntries
 * by amount.
 */
public class AmountComparator implements Comparator<JournalEntry> {

    /**
     * Parameterless constructor setting sort order to its default value
     */
    public AmountComparator() {
        this.sortOrder = new SortOrder(SortOrder.Order.ASC);
    }

    /**
     * Basic constructor setting the desired sort order
     */
    public AmountComparator(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Interface method implementation which does the acutall comparation based
     * on provided sort order
     */
    public int compare(JournalEntry je1, JournalEntry je2) {
        if (sortOrder.getOrder().equals(SortOrder.Order.ASC))
            return je1.getAmount().compareTo(je2.getAmount());
        else
            return je2.getAmount().compareTo(je1.getAmount());
    }

    SortOrder sortOrder;
}
