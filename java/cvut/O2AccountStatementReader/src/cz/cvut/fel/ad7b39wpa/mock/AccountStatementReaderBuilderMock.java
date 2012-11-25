/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.mock;

import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReaderBuilder;
import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.ConfigurationException;
import cz.cvut.fel.ad7b39wpa.core.DataFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.TreeSet;
import org.joda.time.Interval;

/**
 *
 * @author eTeR
 */
public class AccountStatementReaderBuilderMock implements AccountStatementReaderBuilder {

    public AccountStatementReader build(final Callable owner, final Interval accuntablePeriod) throws ConfigurationException {
        return new AccountStatementReader() {

            public Collection<Accountable> read(InputStream accountStatement) throws DataFormatException, IOException {
                Collection<Accountable> accountables = new TreeSet<Accountable>();
                for (int i = 0; i < 50; i++) {
                    accountables.add(AccountableMock.createRandomAccountable(accuntablePeriod));
                }
                return accountables;
            }
        };
    }

}
