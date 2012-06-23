package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.core.AccountablePeriod;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.ServiceType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

public class XLSAccountable implements Accountable {
    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm Z");
    private static final DateFormat unitTimeFormatter = new SimpleDateFormat("mm:ss Z");


    private Date date;
    private ServiceType service;
    private String destination;
    private Callable callee;
    private AccountablePeriod accountablePeriod;
    private long accountedUnits;
    private BigDecimal accountedMoney;
    private boolean freeUnitsApplied;

    public void parseCell(Cell cell) throws ParseException {

        String cellValue = cell.getStringCellValue();

        switch (XLSColumnMap.values()[cell.getColumnIndex()]) {
            case DATE:
                Date tmpDate = (Date) dateFormatter.parse(cellValue);
                this.setDate(tmpDate);
                break;
            case TIME:
                Date time = (Date) timeFormatter.parse((cellValue + " GMT"));
                Date originalDate = this.getDate();
                Date dateTime = new Date(originalDate.getTime() + time.getTime());
                this.setDate(dateTime);
                break;
            case SERVICE:
                ServiceType st = ServiceType.valueOf(cellValue);
                this.setService(st);
                break;
            case DESTINATION:
                this.setDestination(cellValue);
                break;
            case CALLEE:
                if (!cellValue.isEmpty()) {
                    this.setCallee(new XLSCallable(cellValue));
                }
                break;
            case PERIOD:
                this.setAccountablePeriod(getPeriod(cellValue));
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
                this.setAccountedUnits(value);
                break;
            case ACCOUNTED_MONEY:
                this.setAccountedMoney(new BigDecimal(cellValue));
                break;
            case FREE_UNITS_APPLIED:
                this.setFreeUnitsApplied(cellValue.equals("F") ? true : false);
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

    @Override
    public AccountablePeriod getAccountablePeriod() {
        return accountablePeriod;
    }

    @Override
    public void setAccountablePeriod(AccountablePeriod accountablePeriod) {
        this.accountablePeriod = accountablePeriod;
    }

    @Override
    public BigDecimal getAccountedMoney() {
        return accountedMoney;
    }

    @Override
    public void setAccountedMoney(BigDecimal accountedMoney) {
        this.accountedMoney = accountedMoney;
    }

    @Override
    public long getAccountedUnits() {
        return accountedUnits;
    }

    @Override
    public void setAccountedUnits(long accountedUnits) {
        this.accountedUnits = accountedUnits;
    }

    @Override
    public Callable getCallee() {
        return callee;
    }

    @Override
    public void setCallee(Callable callee) {
        this.callee = callee;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public void setFreeUnitsApplied(boolean freeUnitsApplied) {
        this.freeUnitsApplied = freeUnitsApplied;
    }

    @Override
    public ServiceType getService() {
        return service;
    }

    @Override
    public void setService(ServiceType service) {
        this.service = service;
    }

    @Override
    public boolean getFreeUnitsApplied() {
        return freeUnitsApplied;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XLSAccountable other = (XLSAccountable) obj;
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.date != null ? this.date.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return date + ";" + service + ";" + destination + ";" + callee + ";" + accountablePeriod + ";" + accountedUnits + ";" + accountedMoney + ";" + freeUnitsApplied;
    }

    @Override
    public int compareTo(Accountable o) {
        if (o == null) {
            return 1;
        } else if (date == null) {
            return -1;
        }
        return date.compareTo(o.getDate());
    }
}