/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import model.control.remote.AccountableControl;
import cz.cvut.fel.ad7b39wpa.core.AccountStatementReader;
import cz.cvut.fel.ad7b39wpa.core.Accountable;
import cz.cvut.fel.ad7b39wpa.mock.CallableMock;
import cz.cvut.fel.ad7b39wpa.xls.XLSAccountStatementReaderBuilder;
import cz.cvut.fel.ad7b39wpa.xls.XLSInterval;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.User;

/**
 *
 * @author eTeR
 */
@Stateless
public class AccountableControlBean implements AccountableControl {

    @PersistenceContext
    public EntityManager em;

    @Override
    public void importFromFile(byte[] contents, User user) {
        try {
            ByteInputStream file = new ByteInputStream(contents, contents.length);
            AccountStatementReader reader = new XLSAccountStatementReaderBuilder().build(CallableMock.createRandomCallable(), new XLSInterval(new Date(0), new Date(Calendar.getInstance().getTimeInMillis())));
            Collection<Accountable> accountables = reader.read(file);
            for (Accountable acc : accountables) {
                model.Accountable accountable = new model.Accountable();
                accountable.setAccountedMoney(acc.getAccountedMoney().longValue());
                accountable.setAccountedUnits(acc.getAccountedUnits());
                accountable.setCalee((acc.getCallee() != null) ? acc.getCallee().toString() : null);
                accountable.setDate(acc.getDate());
                accountable.setDestination(acc.getDestination());
                accountable.setFreeUnitsApplied(acc.getFreeUnitsApplied() ? (short) 1 : (short) 0);
                accountable.setPeriod(acc.getAccountablePeriod().toString());
                accountable.setService(acc.getService().toString());
                accountable.setUser(user);

                em.persist(accountable);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<model.Accountable> getAllAccountables(User user) {
        Query q = em.createQuery("SELECT a FROM Accountable a WHERE a.user.userId = :userId");
        q.setParameter("userId", user.getUserId());
        return (List<model.Accountable>) q.getResultList();
    }

    @Override
    public Map<String, BigDecimal> getAccountedMoneyPerCalee(User user) {
        Map<String, BigDecimal> accountedPerCalee = new HashMap<String, BigDecimal>();
        List<model.Accountable> allAccountables = getAllAccountables(user);
        for (model.Accountable acc : allAccountables) {
            if ((acc.getCalee() != null) && !acc.getCalee().isEmpty()) {
                BigDecimal total = accountedPerCalee.get(acc.getCalee());
                if (total == null) {
                    total = new BigDecimal(BigInteger.ZERO);
                }
                total = total.add(new BigDecimal(acc.getAccountedMoney()));
                accountedPerCalee.put(acc.getCalee(), total);
            }
        }
        return accountedPerCalee;
    }

    @Override
    public Map<String, Long> getAccountedUnitsPerCalee(User user) {
        Map<String, Long> accountedPerCalee = new HashMap<String, Long>();
        List<model.Accountable> allAccountables = getAllAccountables(user);
        for (model.Accountable acc : allAccountables) {
            if ((acc.getCalee() != null) && !acc.getCalee().isEmpty()) {
                Long total = accountedPerCalee.get(acc.getCalee());
                if (total == null) {
                    total = new Long(0);
                }
                total += acc.getAccountedUnits();
                accountedPerCalee.put(acc.getCalee(), total);
            }
        }
        return accountedPerCalee;
    }

}
