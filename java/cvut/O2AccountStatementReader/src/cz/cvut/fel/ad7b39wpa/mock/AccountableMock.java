package cz.cvut.fel.ad7b39wpa.mock;

import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.core.AccountablePeriod;
import cz.cvut.fel.ad7b39wpa.core.Callable;
import cz.cvut.fel.ad7b39wpa.core.Interval;
import cz.cvut.fel.ad7b39wpa.core.ServiceType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AccountableMock implements Accountable {

    private static Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private static String[] knownDestinations = {"GSM O2 72", "Vodafone 77", "T-Mobile 73", "T-Mobile 605"};
    private static final List<Callable> knownCallables;

    private Date date;
    private ServiceType service;
    private String destination;
    private Callable callee;
    private AccountablePeriod accountablePeriod;
    private long accountedUnits;
    private BigDecimal accountedMoney;
    private boolean freeUnitsApplied;

    static {
        knownCallables = new ArrayList<Callable>(10);
        for (int i = 0; i < 10; i++) {
            knownCallables.add(CallableMock.createRandomCallable());
        }
    }

    public static Accountable createRandomAccountable(final Interval withinInterval) {
        Accountable acc = new AccountableMock();
        acc.setAccountablePeriod(AccountablePeriod.values()[random.nextInt(AccountablePeriod.values().length)]);
        acc.setAccountedMoney(BigDecimal.valueOf(random.nextDouble() * 255).round(new MathContext(5)));
        acc.setAccountedUnits(Math.abs(random.nextLong()) % 255);
        acc.setCallee(knownCallables.get(random.nextInt(knownCallables.size())));
        acc.setDate(new Date(withinInterval.getStartDate().getTime() + (Math.abs(random.nextLong()) % (withinInterval.getEndDate().getTime() - withinInterval.getStartDate().getTime()))));
        acc.setDestination(knownDestinations[random.nextInt(knownDestinations.length)]);
        acc.setFreeUnitsApplied(random.nextBoolean());
        acc.setService(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
        return acc;
    }

    private AccountableMock() {
    }

    public AccountablePeriod getAccountablePeriod() {
        return accountablePeriod;
    }

    public void setAccountablePeriod(AccountablePeriod accountablePeriod) {
        this.accountablePeriod = accountablePeriod;
    }

    public BigDecimal getAccountedMoney() {
        return accountedMoney;
    }

    public void setAccountedMoney(BigDecimal accountedMoney) {
        this.accountedMoney = accountedMoney;
    }

    public long getAccountedUnits() {
        return accountedUnits;
    }

    public void setAccountedUnits(long accountedUnits) {
        this.accountedUnits = accountedUnits;
    }

    public Callable getCallee() {
        return callee;
    }

    public void setCallee(Callable callee) {
        this.callee = callee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setFreeUnitsApplied(boolean freeUnitsApplied) {
        this.freeUnitsApplied = freeUnitsApplied;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

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
        final AccountableMock other = (AccountableMock) obj;
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

    public int compareTo(Accountable o) {
        if (o == null) {
            return 1;
        } else if (date == null) {
            return -1;
        }
        return date.compareTo(o.getDate());
    }
}