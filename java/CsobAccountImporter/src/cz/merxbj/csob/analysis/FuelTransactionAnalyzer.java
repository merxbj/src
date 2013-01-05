/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.analysis;

import cz.merxbj.csob.core.Transaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public class FuelTransactionAnalyzer implements TransactionAnalyzer {

    public void analyze(Collection<Transaction> trans) {
        System.out.printf("Analyzing %d transactions - looking for fuel transactions:\n", trans.size());
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (Transaction tran : trans) {
            if (isFuelTran(tran)) {
                System.out.println(tran);
                total = total.add(tran.getAmount());
            }
        }
        System.out.println("Total fuel expenses: CZK " + total.negate());
    }

    protected static boolean isFuelTran(Transaction tran) {
        if (tran.getComment().contains("CERPACI") ||
            tran.getComment().contains("OMV") ||
            tran.getComment().contains("POSTOLOPRTSKA") ||
            tran.getComment().contains("BENZINA") ||
            tran.getComment().contains("EVROP EVROPSKA") ||
            tran.getComment().contains("PHM") ||
            tran.getComment().contains("BREZENECKA UL.") ||
            tran.getComment().contains("5366") ||
            tran.getComment().contains("VINNA ZAHRADA")) {

            return true;
        }

        return false;
    }
}
