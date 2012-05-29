/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.core.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author jmerxbauer
 */
public class O2ASR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        /**
         * Lets create an anonymous builder here that returns an anonymous reader.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        AccountStatementReaderBuilder builder = new AccountStatementReaderBuilder() {

            @Override
            public AccountStatementReader build(Callable owner, Interval accuntableInterval) throws ConfigurationException {
                return new AccountStatementReader() {

                    @Override
                    public Collection<Accountable> read(InputStream accountStatement) throws DataFormatException, IOException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
        
        /**
         * Use the builder interface to build the reader with anonymous instances
         * of input parameters.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        AccountStatementReader reader = builder.build(new Callable() {

            @Override
            public int getInternationalDialingCode() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setInternationalDialingCode(int internationalDialingCode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getDialingCode() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setDialingCode(int dialingCode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getSubscriberNumber() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setSubscriberNumber(int subscriberNumber) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, new Interval() {

            @Override
            public Date getStartDate() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setStartDate(Date startDate) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Date getEndDate() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setEndDate(Date endDate) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        /**
         * Let's read the collection of accountable events from the account statement.
         * Let's assume that for testing purposes the path to XLS will be passed
         * to the application via the regular parameters as the first parameter
         */
        Collection<Accountable> accountables = reader.read(new FileInputStream(args[0]));
        
        /**
         * And finally print our collection to verify we've read everything.
         */
        for (Accountable acc : accountables) {
            System.out.println(acc);
        }
        
    }
}
