/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author mrneo
 */
public class XLSAccountStatementReader implements AccountStatementReader {

    private DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private DateFormat timeFormatter = new SimpleDateFormat("HH:mm Z");
    private DateFormat unitTimeFormatter = new SimpleDateFormat("mm:ss Z");
    private Interval desiredPeriod;

    @Override
    public Collection<Accountable> read(InputStream stream) throws DataFormatException, IOException {
        Collection<Accountable> accountables = new ArrayList<Accountable>() {};

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = workbook.getSheetAt(0);

            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                XLSAccountable accountable = new XLSAccountable();

                if (row.getRowNum() > 0) {
                    while (cells.hasNext()) {
                        HSSFCell cell = (HSSFCell) cells.next();

                        this.parseInformation(accountable, cell);
                    }
                    accountables.add(accountable);
                }
            }
        } catch (Exception e) {
            System.out.println("Something is wrong with XLS file! Error: " + e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return this.checkAccountableValidity(accountables);
    }

    public void setDesiredPeriod(Interval accountablePeriod) {
        this.desiredPeriod = accountablePeriod;
    }
    
    private void parseInformation(Accountable accountable, HSSFCell cell) throws UnsupportedOperationException, ParseException {

        String cellValue = cell.getStringCellValue();

        switch (XLSMap.values()[cell.getColumnIndex()]) {
            case DATE:
                Date date = (Date) dateFormatter.parse(cellValue);
                accountable.setDate(date);
                break;
            case TIME:
                Date time = (Date) timeFormatter.parse((cellValue + " GMT"));
                Date originalDate = accountable.getDate();
                Date dateTime = new Date(originalDate.getTime() + time.getTime());
                accountable.setDate(dateTime);
                break;
            case SERVICE:
                ServiceType st = ServiceType.valueOf(cellValue);
                accountable.setService(st);
                break;
            case DESTINATION:
                accountable.setDestination(cellValue);
                break;
            case CALLEE:
                if (!cellValue.isEmpty()) {
                    accountable.setCallee(new XLSCallable(cellValue));
                }
                break;
            case PERIOD:
                accountable.setAccountablePeriod(getPeriod(cellValue));
                break;
            case ACCOUNTED_UNITS:
                long value = 0;
                if (isTime(cellValue)) {
                    Date unitTime = (Date) unitTimeFormatter.parse((cellValue + " GMT"));
                    value = unitTime.getTime() / 1000;
                }
                else {
                    value = Integer.parseInt(cellValue);
                }
                accountable.setAccountedUnits(value);
                break;
            case ACCOUNTED_MONEY:
                accountable.setAccountedMoney(new BigDecimal(cellValue));
                break;
            case FREE_UNITS_APPLIED:
                accountable.setFreeUnitsApplied(cellValue.equals("F") ? true : false);
                break;
            default:
                throw new UnsupportedOperationException("Unmapped XLS column on position " + cell.getColumnIndex() + "!");
        }
    }
    
    private boolean isTime(String cellValue) {
        return cellValue.contains(":");
    }
    
    private AccountablePeriod getPeriod(String cellValue) {
        if (cellValue.isEmpty()) {
            return AccountablePeriod.NOT_APPLICABLE;
        }
        else if (cellValue.equals("7h-19h")) {
            return AccountablePeriod.WITHIN_PEAK;
        }
        else if (cellValue.equals("19h-7h")) {
            return AccountablePeriod.OUTISDE_PEAK;
        }
        else if (cellValue.toUpperCase().equals("VŽDY")) {
            return AccountablePeriod.ALWAYS;
        }
        else if (cellValue.toUpperCase().equals("SO-NE")) {
            return AccountablePeriod.WEEKEND;
        }
        else {
            return AccountablePeriod.UNKNOWN;
        }
    }

    private Collection<Accountable> checkAccountableValidity(Collection<Accountable> accountables) {
        Collection<Accountable> validatedAccountables = new ArrayList<Accountable>() {};
        Date startDate = desiredPeriod.getStartDate();
        Date endDate = desiredPeriod.getEndDate();

        if (startDate.getTime() == 0 && endDate.getTime() == 0) {
            return accountables;
        }

        for (Accountable accountable : accountables) {
            if (accountable.getDate().after(startDate) && accountable.getDate().before(endDate)) {
                validatedAccountables.add(accountable);
            }
        }

        return validatedAccountables;
    }
}
