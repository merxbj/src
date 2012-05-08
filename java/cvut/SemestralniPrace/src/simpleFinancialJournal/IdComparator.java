package simpleFinancialJournal;

import java.util.*;

/**
 * Provides comparator implementation for sorting an array of JournalEntries
 * by ID.
 */
public class IdComparator implements Comparator <JournalEntry> {

    /**
     * Parameterless constructor setting sort order to its default value
     */
    public IdComparator() {
        order = new SortOrder(SortOrder.Order.ASC);
    }

    /**
     * Basic constructor setting the desired sort order
     */
    public IdComparator(SortOrder order) {
        this.order = order;
    }

    /**
     * Interface method implementation which does the acutall comparation based
     * on provided sort order
     */
    public int compare(JournalEntry je1, JournalEntry je2) {
        if (order.getOrder().equals(SortOrder.Order.ASC))
            return je1.compareTo(je2);
        else
            return je2.compareTo(je1);
    }

    SortOrder order;
}
