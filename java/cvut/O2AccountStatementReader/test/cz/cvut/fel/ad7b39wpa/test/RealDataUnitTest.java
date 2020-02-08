/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.test;

import org.joda.time.Interval;
import org.joda.time.DateTime;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReaderBuilder;
import cz.cvut.fel.ad7b39wpa.core.ConfigurationException;
import cz.cvut.fel.ad7b39wpa.core.DataFormatException;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.Accountable;
import java.util.Collection;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.core.AccountablePeriod;
import cz.cvut.fel.ad7b39wpa.core.ServiceType;
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
            Interval period = new Interval(new DateTime(2012, 01, 01, 0, 0), new DateTime(2013, 01, 01, 0, 0));
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
            Interval period = new Interval(new DateTime(2012, 04, 01, 0, 0), new DateTime(2012, 05, 01, 0, 0));
            Collection<Accountable> accountables = readAccountables(period);

            BigDecimal total = new BigDecimal(BigInteger.ZERO);
            for (Accountable acc : accountables) {
                total = total.add(acc.getAccountedMoney());
            }

            assertEquals(new BigDecimal("367.33"), total);
        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            try {
                if (testData != null) {
                    testData.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    @Test
    public void gprsMemberWiseTest() throws Exception {
        try {
            Interval period = new Interval(new DateTime(2012, 01, 01, 0, 0), new DateTime(2013, 01, 01, 0, 0));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[0];

            assertEquals(AccountablePeriod.NOT_APPLICABLE, acc.getAccountablePeriod());
            assertEquals(new BigDecimal(0), acc.getAccountedMoney());
            assertEquals(2, acc.getAccountedUnits());
            assertNull(acc.getCallee());
            assertEquals(new DateTime(2012, 4, 8, 0, 0, 0), acc.getDate());
            assertEquals("Mobilní data, Internet (čas)", acc.getDestination());
            assertFalse(acc.getFreeUnitsApplied());
            assertEquals(ServiceType.GPRS, acc.getService());

        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            if (testData != null) {
                try {
                    testData.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    @Test
    public void gsmMemberWiseTest() throws Exception {
        try {
            Interval period = new Interval(new DateTime(2012, 01, 01, 0, 0), new DateTime(2013, 01, 01, 0, 0));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[3];

            assertEquals(AccountablePeriod.WEEKEND, acc.getAccountablePeriod());
            assertEquals(new BigDecimal(0), acc.getAccountedMoney());
            assertEquals(60, acc.getAccountedUnits());
            assertEquals(new Callable(420, 605, 550012), acc.getCallee());
            assertEquals(new DateTime(2012, 4, 8, 17, 50, 0), acc.getDate());
            assertEquals("Vodafone", acc.getDestination());
            assertTrue(acc.getFreeUnitsApplied());
            assertEquals(acc.getService(), ServiceType.TEL);


        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            if (testData != null) {
                try {
                    testData.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    @Test
    public void textMemberWiseTest() throws Exception {
        try {
            Interval period = new Interval(new DateTime(2012, 01, 01, 0, 0), new DateTime(2013, 01, 01, 0, 0));
            Collection<Accountable> accountables = readAccountables(period);
            Accountable acc = (accountables.toArray(new Accountable[accountables.size()]))[4];

            assertEquals(AccountablePeriod.ALWAYS, acc.getAccountablePeriod());
            assertEquals(new BigDecimal("1.33"), acc.getAccountedMoney());
            assertEquals(1, acc.getAccountedUnits());
            assertEquals(new Callable(420, 731, 108199), acc.getCallee());
            assertEquals(new DateTime(2012, 4, 8, 17, 56, 0), acc.getDate());
            assertEquals("T-Mobile 73", acc.getDestination());
            assertFalse(acc.getFreeUnitsApplied());
            assertEquals(acc.getService(), ServiceType.TEXT);

        } catch (Exception ex) {
            fail(ex.toString());
        }
        finally {
            if (testData != null) {
                try {
                    testData.close();
                } catch (IOException ex) {

                }
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