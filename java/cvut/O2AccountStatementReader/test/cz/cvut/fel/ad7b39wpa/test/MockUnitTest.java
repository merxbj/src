/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.core.DataFormatException;
import java.io.IOException;
import java.util.Collection;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.core.ConfigurationException;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.mock.AccountStatementReaderBuilderMock;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReaderBuilder;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author merxbj
 */
public class MockUnitTest {

    private final static long ONE_DAY_AS_MILLISECONDS = 24L * 60L * 60L * 1000L;
    private final static Random random = new Random(DateTime.now().getMillis());

    public MockUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createAndParseMockData() throws ConfigurationException, DataFormatException, IOException {
        /**
         * Lets create an mock builder here that returns a mocj reader.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        AccountStatementReaderBuilder builder = new AccountStatementReaderBuilderMock();

        /**
         * Use the builder interface to build a reader.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        Callable owner = CallableMock.createRandomCallable();
        Interval period = createRandomInterval(360, 30);
        AccountStatementReader reader = builder.build(owner, period);

        /**
         * Let's read the collection of accountable events.
         */
        Collection<Accountable> accountables = reader.read(null);

        /**
         * And finally print our collection to verify we've read everything.
         */
        for (Accountable acc : accountables) {
            System.out.println(acc);
        }
    }

    private static Interval createRandomInterval(int maxDaysBack, int daysDuration) {
        if (maxDaysBack < daysDuration) {
            throw new RuntimeException(String.format("maxDaysBack(%d) < daysDuration(%d)", maxDaysBack, daysDuration));
        }

        int daysBack = random.nextInt(maxDaysBack);
        long startMilis = DateTime.now().getMillis() - (daysBack * ONE_DAY_AS_MILLISECONDS);

        return new Interval(startMilis, startMilis + (daysDuration * ONE_DAY_AS_MILLISECONDS));
    }

}