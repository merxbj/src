/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.analysis;

import cz.merxbj.csob.core.Transaction;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public class SuspiciousNonFuelTranAnalyzer extends FuelTransactionAnalyzer {

    @Override
    public void analyze(Collection<Transaction> trans) {
        System.out.printf("Analyzing %d transactions - looking for suspicious non-fuel transactions:\n", trans.size());
        for (Transaction tran : trans) {
            if (!isFuelTran(tran)) {
                if ((tran.getAmount().compareTo(new BigDecimal(-1600.0)) < 0) && (tran.getAmount().compareTo(new BigDecimal(-2000.0)) > 0)) {
                    System.out.println(tran);
                }
            }
        }
    }

}
