/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import cz.merxbj.csob.analysis.AnalysisDriver;
import cz.merxbj.csob.analysis.TransactionAnalyzer;
import java.util.Collection;
import java.util.Scanner;

/**
 *
 * @author merxbj
 */
public class CommandLineAnalysisDriver implements AnalysisDriver {

    private final Collection<TransactionAnalyzer> analyzers;

    public CommandLineAnalysisDriver(final Collection<TransactionAnalyzer> analyzers) {
        this.analyzers = analyzers;
    }

    public void analyze(Collection<Transaction> trans) {
        System.out.println("Begining the analysis:");
        for (TransactionAnalyzer analyzer : analyzers) {
            analyzer.analyze(trans);
        }
        new Scanner(System.in).nextLine();
    }

}
