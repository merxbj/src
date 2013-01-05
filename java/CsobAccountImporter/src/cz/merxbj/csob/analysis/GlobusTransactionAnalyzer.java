/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.analysis;

import cz.merxbj.csob.core.Transaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author merxbj
 */
public class GlobusTransactionAnalyzer implements TransactionAnalyzer {

    public void analyze(Collection<Transaction> trans) {
        System.out.printf("Analyzing %d transactions - looking for globus transactions:\n", trans.size());
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        BigDecimal subTotal = new BigDecimal(BigInteger.ZERO);
        List<Transaction> sorted = new ArrayList<Transaction>(trans);
        Collections.sort(sorted, new TranDateAscendingComparer());
        int count = 0;
        for (Transaction tran : sorted) {
            if (isGlobusTran(tran)) {
                System.out.println(tran);
                total = total.add(tran.getAmount());
                count++;
            }
        }
        System.out.println("Total globus expenses: CZK " + total.negate());
        System.out.println("Avarage GLOBUS sale: CZK " + total.negate().doubleValue() / count);
    }

    protected static boolean isGlobusTran(Transaction tran) {
        if (tran.getComment().contains("GLOBUS")) {
            return true;
        }

        return false;
    }

    private static class TranDateAscendingComparer implements Comparator<Transaction> {

        public int compare(Transaction o1, Transaction o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
        
    }

}
