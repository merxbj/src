package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.core.AccountablePeriod;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.ServiceType;
import java.math.BigDecimal;
import java.util.Date;

public class XLSAccountable implements Accountable {
    private Date date;
    private ServiceType service;
    private String destination;
    private Callable callee;
    private AccountablePeriod accountablePeriod;
    private long accountedUnits;
    private BigDecimal accountedMoney;
    private boolean freeUnitsApplied;

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