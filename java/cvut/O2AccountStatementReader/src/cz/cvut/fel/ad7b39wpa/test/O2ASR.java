/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.test;

import cz.cvut.fel.ad7b39wpa.xls.XLSAccountStatementReaderBuilder;
import cz.cvut.fel.ad7b39wpa.core.*;
import cz.cvut.fel.ad7b39wpa.mock.AccountStatementReaderBuilderMock;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import cz.cvut.fel.ad7b39wpa.mock.IntervalMock;
import cz.cvut.fel.ad7b39wpa.xls.XLSInterval;
import java.io.File;
import java.io.FileInputStream;
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
        
        /* TODO: change this
         */
        String filename = args[0];
        File xlsFile = new File(filename);
        if (!xlsFile.exists()) {
            throw new Exception("File not exists!");
        }
        
        XLSAccountStatementReaderBuilder builder = new XLSAccountStatementReaderBuilder();
        InputStream is = new FileInputStream(xlsFile);

        /**
         * Use the builder interface to build a reader.
         * It is on the actual implementor discretion to supply a valid and working implementation.
         */
        Callable owner = CallableMock.createRandomCallable();
        //Interval period = IntervalMock.createRandomInterval(360, 30);
        Interval period = new XLSInterval(new Date(112,03,01), new Date(112,04,01)); // For testing purposes, have to be removed after!!!
        AccountStatementReader reader = builder.build(owner, period);

        /**
         * Let's read the collection of accountable events.
         */
        Collection<Accountable> accountables = reader.read(is);

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