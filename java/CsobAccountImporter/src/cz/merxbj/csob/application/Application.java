/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.application;

import cz.merxbj.csob.core.CommandLineAnalysisDriver;
import cz.merxbj.csob.core.AnalysisDriver;
import cz.merxbj.csob.core.CsobParserFactory;
import cz.merxbj.csob.core.CsobParser;
import cz.merxbj.csob.core.FuelTransactionAnalyzer;
import cz.merxbj.csob.core.GlobusTransactionAnalyzer;
import cz.merxbj.csob.core.SuspiciousNonFuelTranAnalyzer;
import cz.merxbj.csob.core.Transaction;
import cz.merxbj.csob.core.TransactionAnalyzer;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

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
            Collection<TransactionAnalyzer> analyzers = new ArrayList<TransactionAnalyzer>();
            analyzers.add(new FuelTransactionAnalyzer());
            analyzers.add(new SuspiciousNonFuelTranAnalyzer());
            analyzers.add(new GlobusTransactionAnalyzer());
            AnalysisDriver driver = new CommandLineAnalysisDriver(analyzers);
            driver.analyze(trans);
        } catch (IOException ex) {
            try {
                fis.close();
            } catch (IOException iex) {
                
            }
        }

    }
}
