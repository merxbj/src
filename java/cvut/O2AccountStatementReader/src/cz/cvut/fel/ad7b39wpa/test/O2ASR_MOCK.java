/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.core.*;
import cz.cvut.fel.ad7b39wpa.mock.AccountStatementReaderBuilderMock;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import cz.cvut.fel.ad7b39wpa.mock.IntervalMock;
import java.util.Collection;

/**
 *
 * @author jmerxbauer
 */
public class O2ASR_MOCK {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
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
        Interval period = IntervalMock.createRandomInterval(360, 30);
        AccountStatementReader reader = builder.build(owner, period);

        /**
         * Let's read the collection of accountable events.
         */
        Collection<Accountable> accountables = reader.read(null);

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
