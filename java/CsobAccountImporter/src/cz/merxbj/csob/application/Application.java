/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.application;

import cz.merxbj.csob.core.CsobParserFactory;
import cz.merxbj.csob.core.CsobParser;
import cz.merxbj.csob.core.DatabaseImporter;
import cz.merxbj.csob.core.Transaction;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public class Application {

    public static void main(String[] args) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(args[0]);
            CsobParser parser = CsobParserFactory.createParser(args[0]);
            Collection<Transaction> trans = parser.parse(fis);
            DatabaseImporter importer = new DatabaseImporter();
            importer.importTransactions(trans);
        } catch (IOException ex) {
            try {
                fis.close();
            } catch (IOException iex) {
                
            }
        }


    }
    
}
