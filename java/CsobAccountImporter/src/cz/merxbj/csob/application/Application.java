/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.application;

import cz.merxbj.csob.core.CsobParser;
import cz.merxbj.csob.core.CsobParserFactory;
import cz.merxbj.csob.core.Transaction;
import cz.merxbj.csob.core.TransactionImporter;
import cz.merxbj.csob.db.DatabaseConnectionInfo;
import cz.merxbj.csob.db.DatabaseImporter;
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
            /*Collection<TransactionAnalyzer> analyzers = new ArrayList<TransactionAnalyzer>();
            analyzers.add(new FuelTransactionAnalyzer());
            analyzers.add(new SuspiciousNonFuelTranAnalyzer());
            analyzers.add(new GlobusTransactionAnalyzer());
            AnalysisDriver driver = new CommandLineAnalysisDriver(analyzers);
            driver.analyze(trans);*/
            DatabaseConnectionInfo ci = new DatabaseConnectionInfo();
            ci.setHost("localhost");
            ci.setPort("3306");
            ci.setUser("root");
            ci.setPassword("prsten");
            ci.setDbname("MyPersonalAccountDb");
            TransactionImporter importer = new DatabaseImporter(ci);
            importer.importTransactions(trans);
        } catch (IOException ex) {
            try {
                fis.close();
            } catch (IOException iex) {
                
            }
        }

    }
}
