/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.core.AccountStatementReaderBuilder;
import cz.cvut.fel.ad7b39wpa.core.ConfigurationException;
import cz.cvut.fel.ad7b39wpa.core.DataFormatException;
import org.junit.Before;
import org.junit.After;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.Accountable;
import java.util.Collection;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.xls.XLSInterval;
import cz.cvut.fel.ad7b39wpa.core.Interval;
import cz.cvut.fel.ad7b39wpa.core.ServiceType;
import java.util.Date;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import java.io.InputStream;
import cz.cvut.fel.ad7b39wpa.xls.XLSAccountStatementReaderBuilder;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author merxbj
 */
public class RealDataUnitTest {

    private InputStream testData;
    private AccountStatementReaderBuilder builder;

    public RealDataUnitTest() {
         builder = new XLSAccountStatementReaderBuilder();
    }

    /**
     * Try read all the data from the input stream
     * @throws Exception
     */
    @Test
    public void basicTest() throws Exception {

        try {
            Interval period = new XLSInterval(new Date(112, 01, 01), new Date(112, 11, 01));
            Collection<Accountable> accountables = readAccountables(period);

            /**
             * And finally print our collection to verify we've read everything.
             */

            BigDecimal total = new BigDecimal(BigInteger.ZERO);
            for (Accountable acc : accountables) {
                total = total.add(acc.getAccountedMoney());
            }

            assertEquals(total, new BigDecimal("533.10"));
        } finally {
            try {
                testData.close();
            } catch (IOException ex) {

            }
        }
    }

    @Test
    public void intervalTest() throws Exception {
        try {
            Interval period = new XLSInterval(new Date(112, 03, 01), new Date(112, 04, 01));
            Collection<Accountable> accountables = readAccountables(period);

            BigDecimal total = new BigDecimal(BigInteger.ZERO);
            for (Accountable acc : accountables) {
                total = total.add(acc.getAccountedMoney());
            }

            assertEquals(new BigDecimal("367.33"), total);
        } finally {
            try {
                testData.close();
            } catch (IOException ex) {

            }
        }
    }

    @Test
    public void gprsMemberWiseTest() throws Exception {
        try {
            Interval period = new XLSInterval(new Date(112, 00, 01), new Date(112, 11, 01));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[0];

            assertEquals(acc.getService(), ServiceType.GPRS);
            

        } finally {
            try {
                testData.close();
            } catch (IOException ex) {

            }
        }
    }

    @Test
    public void gsmMemberWiseTest() throws Exception {
        try {
            Interval period = new XLSInterval(new Date(112, 00, 01), new Date(112, 11, 01));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[3];

            assertEquals(acc.getService(), ServiceType.TEL);


        } finally {
            try {
                testData.close();
            } catch (IOException ex) {

            }
        }
    }

    @Test
    public void textMemberWiseTest() throws Exception {
        try {
            Interval period = new XLSInterval(new Date(112, 00, 01), new Date(112, 11, 01));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[4];

            assertEquals(acc.getService(), ServiceType.TEXT);


        } finally {
            try {
                testData.close();
            } catch (IOException ex) {

            }
        }
    }

    private Collection<Accountable> readAccountables(Interval period) throws ConfigurationException, IOException, DataFormatException {

        /**
         * Read the well known data from the resource
         */
        testData = this.getClass().getResourceAsStream("data/eucet_test.xls");
        assertNotNull(testData);

        /**
         * Use the builder interface to build a reader. We don't really care about
         * the owner any more. Might be implemented later.
         */
        Callable owner = CallableMock.createRandomCallable();
        AccountStatementReader reader = builder.build(owner, period);
        assertNotNull(reader);

        /**
         * Let's read the collection of accountable events.
         */
        Collection<Accountable> accountables = reader.read(testData);
        assertNotNull(accountables);

        return accountables;
    }

}