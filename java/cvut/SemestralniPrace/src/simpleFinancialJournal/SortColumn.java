package simpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class SortColumn {

    public SortColumn(String rawName) {
        this.column = parse(rawName);
    }

    public SortColumn(Column column) {
        this.column = column;
    }

    public Column parse(String rawName) {
        if (rawName.compareTo("id") == 0) {
            return Column.ID;
        } else if (rawName.compareTo("amount") == 0) {
            return Column.AMOUNT;
        } else if (rawName.compareTo("description") == 0) {
            return Column.DESCRIPTION;
        } else {
            return Column.UNSUPPORTED;
        }
    }

    @Override
    public String toString() {
        return column.toString();
    }

    public Column getColumn() {
        return column;
    }

    private Column column;

    public enum Column {

        ID, AMOUNT, DESCRIPTION, UNSUPPORTED
    }
}
