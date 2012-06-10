/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import model.Accountable;
import model.control.AccountableControl;
import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;

/**
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class SummaryBean {

    @EJB
    AccountableControl accountables;

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    public void setSecurity(SecurityBean security) {
        this.security = security;
    }

    public List<Accountable> getAccountables() {
        return accountables.getAllAccountables(security.getLoggedInUser());
    }

    public CartesianChartModel getMostExpensiveModel() {
        Map<String, BigDecimal> accountedPerCalee = accountables.getAccountedPerCalee(security.getLoggedInUser());
        CartesianChartModel chartModel = new CartesianChartModel();
        ChartSeries money = new ChartSeries();
        money.setLabel("Accounted Money");
        chartModel.addSeries(money);

        BigDecimal totalAccounted = new BigDecimal(BigInteger.ZERO);
        for (BigDecimal accounted : accountedPerCalee.values()) {
            totalAccounted = totalAccounted.add(accounted);
        }
        for (Entry<String, BigDecimal> entry : accountedPerCalee.entrySet()) {
            double ratio = entry.getValue().doubleValue() / totalAccounted.doubleValue();
            if (ratio > 0.01) {
                money.set(entry.getKey(), entry.getValue().longValue());
            }
        }

        return chartModel;
    }

}
