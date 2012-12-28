/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import cz.merxbj.csob.core.Transaction;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public interface AnalysisDriver {

    public void analyze(Collection<Transaction> trans);

}
