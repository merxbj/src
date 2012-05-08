package simpleFinancialJournal;

import java.lang.*;

/**
 *
 * @author eTeR
 */
public class JournalException extends Exception {

    @Override
    public String toString() {
        return getMessage();
    }

    public JournalException(String message) {
        super(message);
    }

}
