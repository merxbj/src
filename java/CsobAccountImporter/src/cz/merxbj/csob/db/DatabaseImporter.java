/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.csob.db;

import cz.merxbj.csob.core.Transaction;
import cz.merxbj.csob.core.TransactionImporter;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public class DatabaseImporter implements TransactionImporter {
    
    private DatabaseConnectionInfo ci;

    public DatabaseImporter(DatabaseConnectionInfo ci) {
        this.ci = ci;
    }

    public void importTransactions(Collection<Transaction> trans) {
        DatabaseConnection con = new DatabaseConnection(ci);
        try
        {
            for (Transaction tran : trans) {
                String insertStatementTemplate = "INSERT INTO Transaction (date, amount, ks, vs, ss, type, offset_account_name, offset_account_number, comment) "
                        + "VALUES ('%s', %f, '%s', '%s', '%s', '%s', '%s', '%s', '%s')";
                String insertStatement = String.format(insertStatementTemplate, 
                        tran.getDate().toString(),
                        tran.getAmount().doubleValue(),
                        tran.getKs(),
                        tran.getVs(),
                        tran.getSs(),
                        tran.getType(),
                        tran.getOffsetAccountName(),
                        tran.getOffsetAccountNumber(),
                        tran.getComment());
                System.out.println(insertStatement);
                con.executeCommand(insertStatement);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            con.close();
        }
    }
    
}
