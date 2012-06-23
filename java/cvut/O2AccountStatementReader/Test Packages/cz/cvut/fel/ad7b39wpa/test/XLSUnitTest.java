/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.Accountable;
import java.util.Collection;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.xls.XLSInterval;
import cz.cvut.fel.ad7b39wpa.core.Interval;
import java.util.Date;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import java.io.InputStream;
import cz.cvut.fel.ad7b39wpa.xls.XLSAccountStatementReaderBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author merxbj
 */
public class XLSUnitTest {

    @Test
    public void hello() throws Exception {

        /**
         * Read the well known data from the resource
         */
        InputStream testData = this.getClass().getResourceAsStream("data/eucet_test.xls");
        assertNotNull(testData);

        XLSAccountStatementReaderBuilder builder = new XLSAccountStatementReaderBuilder();

        /**
         * Use the builder interface to build a reader.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        Callable owner = CallableMock.createRandomCallable();
        Interval period = new XLSInterval(new Date(112,03,01), new Date(112,07,01)); // For testing purposes, have to be removed after!!!
        AccountStatementReader reader = builder.build(owner, period);

        /**
         * Let's read the collection of accountable events.
         */
        Collection<Accountable> accountables = reader.read(testData);

        /**
         * And finally print our collection to verify we've read everything.
         */

        System.out.println("Owner: " + owner);
        System.out.println("Period: " + period);
        System.out.println("");
        for (Accountable acc : accountables) {
            System.out.println(acc);
        }

    }

}