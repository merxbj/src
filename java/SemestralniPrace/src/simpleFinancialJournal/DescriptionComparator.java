package simpleFinancialJournal;

import java.util.*;

/**
 * Provides comparator implementation for sorting an array of JournalEntries
 * by description.
 */
public class DescriptionComparator implements Comparator<JournalEntry> {

    /**
     * Parameterless constructor setting sort order to its default value
     */
    public DescriptionComparator() {
        this.sortOrder = new SortOrder(SortOrder.Order.ASC);
    }

    /**
     * Basic constructor setting the desired sort order
     */
    public DescriptionComparator(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Interface method implementation which does the acutall comparation based
     * on provided sort order
     */
    public int compare(JournalEntry je1, JournalEntry je2) {
        if (sortOrder.getOrder().equals(SortOrder.Order.ASC))
            return je1.description.compareToIgnoreCase(je2.description);
        else
            return je2.description.compareToIgnoreCase(je1.description);
    }

    SortOrder sortOrder;
}
