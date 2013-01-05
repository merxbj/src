/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.analysis;

import cz.merxbj.csob.core.Transaction;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public interface TransactionAnalyzer {
    void analyze(Collection<Transaction> trans);
}
