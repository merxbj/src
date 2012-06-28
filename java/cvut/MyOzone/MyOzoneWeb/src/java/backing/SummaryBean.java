/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import model.Accountable;
import model.control.remote.AccountableControl;
import model.utility.AccountableAscendingComparer;

/**
 * Backing bean for the account summary page.
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class SummaryBean {

    @EJB
    AccountableControl accountables;

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    /**
     * This is required by the @ManagedProperty to set the actual instance.
     * @param security
     */
    public void setSecurity(SecurityBean security) {
        this.security = security;
    }

    /**
     * Gets all accountables of the currently logged in user.
     * @return all accountables
     */
    public List<Accountable> getAccountables() {
        List<Accountable> sortedAccountables = new ArrayList<Accountable>(accountables.getAllAccountables(security.getLoggedInUser()));
        Collections.sort(sortedAccountables, new AccountableAscendingComparer());
        return sortedAccountables;
    }

    /**
     * Gets the aggregated data of the most expensive callees.
     * What that means is that we get aggregated data per callee from the
     * appropriate EJB and get those calees which total of accounted money is over
     * 5% of the grand total of all accounted money across the board
     * @return the aggregated data
     */
    public List<CaleeAgregatedData> getMostExpensiveCallees() {
        Map<String, BigDecimal> accountedPerCalee = accountables.getAccountedMoneyPerCalee(security.getLoggedInUser());
        List<CaleeAgregatedData> mostExpensiveCallees = new ArrayList<CaleeAgregatedData>();

        BigDecimal totalAccounted = new BigDecimal(BigInteger.ZERO);
        for (BigDecimal accounted : accountedPerCalee.values()) {
            totalAccounted = totalAccounted.add(accounted);
        }
        for (Entry<String, BigDecimal> entry : accountedPerCalee.entrySet()) {
            double ratio = entry.getValue().doubleValue() / totalAccounted.doubleValue();
            if (ratio > 0.05) {
                CaleeAgregatedData data = new CaleeAgregatedData();
                data.setCallee(entry.getKey());
                data.setMoney(entry.getValue().doubleValue());
                mostExpensiveCallees.add(data);
            }
        }

        Collections.sort(mostExpensiveCallees);
        return mostExpensiveCallees;
    }

    /**
     * Gets the aggregated data of all callees.
     * @return aggregate data of all callees
     */

    public List<CaleeAgregatedData> getCalleesAggregatedData() {
        Map<String, Long> accountedUnitsPerCalee = accountables.getAccountedUnitsPerCalee(security.getLoggedInUser());
        Map<String, BigDecimal> accountedMoneyPerCalee = accountables.getAccountedMoneyPerCalee(security.getLoggedInUser());
        List<CaleeAgregatedData> calleesAggregatedData = new ArrayList<CaleeAgregatedData>();

        for (String accounted : accountedMoneyPerCalee.keySet()) {
            CaleeAgregatedData data = new CaleeAgregatedData();
            data.setCallee(accounted);
            data.setMoney(accountedMoneyPerCalee.get(accounted).doubleValue());
            data.setUnits(accountedUnitsPerCalee.get(accounted));
            calleesAggregatedData.add(data);
        }

        Collections.sort(calleesAggregatedData);
        return calleesAggregatedData;
    }

    /**
     * Helper class to hold the aggregated data per callee (units and money)
     */
    public static class CaleeAgregatedData implements Comparable<CaleeAgregatedData> {
        private String callee;
        private double money;
        private long units;

        public String getCallee() {
            return callee;
        }

        public void setCallee(String callee) {
            this.callee = callee;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public long getUnits() {
            return units;
        }

        public void setUnits(long units) {
            this.units = units;
        }

        @Override
        public int compareTo(CaleeAgregatedData o) {
            int compare = new Double(o.getMoney()).compareTo(money);
            if (compare == 0) {
                compare = new Long(o.getUnits()).compareTo(units);
            }
            return compare;
        }

    }

}
