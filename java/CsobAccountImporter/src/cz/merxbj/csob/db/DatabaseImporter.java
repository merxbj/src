/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.csob.db;

import cz.merxbj.csob.core.Transaction;
import cz.merxbj.csob.core.TransactionImporter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Locale;

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
                String insertStatement = String.format(Locale.ENGLISH,insertStatementTemplate, 
                        tran.getDate().toLocalDate().toString(),
                        tran.getAmount().doubleValue(),
                        tran.getKs(),
                        tran.getVs(),
                        tran.getSs(),
                        tran.getType(),
                        tran.getOffsetAccountName(),
                        tran.getOffsetAccountNumber(),
                        tran.getComment());
                FileWriter writer = new FileWriter("/users/merxbj/temp/dump.sql", true);
                try
                {
                   writer.write(insertStatement);
                   writer.write(System.lineSeparator());
                }
                finally
                {
                    writer.close();
                }
                con.executeCommand(insertStatement);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            con.close();
        }
    }
    
}
