package cz.cvut.fel.ad7b39wpa.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Reader of the O2 account statement provided as an XLS sheet.
 * @author jmerxbauer
 */
public interface AccountStatementReader {
    /**
     * Reads the O2 account statement provided as an XLS sheet.
     * @param accountStatement the XLS account statement to be read.
     * @return the collection of accountable events reported in the supplied account statement
     * @throws DataFormatException if the supplied account statement doesn't have the expected format
     * @throws IOException if the supplied account statement cannot be read
     */
    public Collection<Accountable> read(InputStream accountStatement) throws DataFormatException, IOException;
}
