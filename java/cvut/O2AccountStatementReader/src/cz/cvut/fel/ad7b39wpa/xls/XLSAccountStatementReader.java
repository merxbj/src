/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 *
 * @author mrneo
 */
public class XLSAccountStatementReader implements AccountStatementReader {

    private Interval desiredPeriod;

    @Override
    public Collection<Accountable> read(InputStream stream) throws DataFormatException, IOException {

        Collection<Accountable> accountables = new ArrayList<Accountable>();

        try {
            Workbook workbook = new HSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // skip the first row, it is just the column header
                if (row.getRowNum() > 0) {
                    XLSAccountable accountable = new XLSAccountable();
                    for (Cell cell : row) {
                        accountable.parseCell(cell);
                    }
                    accountables.add(accountable);
                }
            }
        } catch (Exception e) {
            throw new DataFormatException("Something is wrong with XLS file", e);
        }

        return filterOutUndesiredAccountables(accountables);
    }

    public void setDesiredPeriod(Interval accountablePeriod) {
        this.desiredPeriod = accountablePeriod;
    }

    private Collection<Accountable> filterOutUndesiredAccountables(Collection<Accountable> accountables) {
        Collection<Accountable> validatedAccountables = new ArrayList<Accountable>() {};
        DateTime startDate = desiredPeriod.getStart();
        DateTime endDate = desiredPeriod.getEnd();

        if (startDate.getMillis() == 0 && endDate.getMillis() == 0) {
            return accountables;
        }

        for (Accountable accountable : accountables) {
            if (accountable.getDate().isAfter(startDate) && accountable.getDate().isBefore(endDate)) {
                validatedAccountables.add(accountable);
            }
        }

        return validatedAccountables;
    }
}
