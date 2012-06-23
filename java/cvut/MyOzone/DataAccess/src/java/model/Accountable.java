/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author eTeR
 */
@Entity
@Table(name = "accountable")
@NamedQueries({
    @NamedQuery(name = "Accountable.findAll", query = "SELECT a FROM Accountable a"),
    @NamedQuery(name = "Accountable.findByAccountableId", query = "SELECT a FROM Accountable a WHERE a.accountableId = :accountableId"),
    @NamedQuery(name = "Accountable.findByDate", query = "SELECT a FROM Accountable a WHERE a.date = :date"),
    @NamedQuery(name = "Accountable.findByService", query = "SELECT a FROM Accountable a WHERE a.service = :service"),
    @NamedQuery(name = "Accountable.findByDestination", query = "SELECT a FROM Accountable a WHERE a.destination = :destination"),
    @NamedQuery(name = "Accountable.findByCalee", query = "SELECT a FROM Accountable a WHERE a.calee = :calee"),
    @NamedQuery(name = "Accountable.findByPeriod", query = "SELECT a FROM Accountable a WHERE a.period = :period"),
    @NamedQuery(name = "Accountable.findByAccountedUnits", query = "SELECT a FROM Accountable a WHERE a.accountedUnits = :accountedUnits"),
    @NamedQuery(name = "Accountable.findByAccountedMoney", query = "SELECT a FROM Accountable a WHERE a.accountedMoney = :accountedMoney"),
    @NamedQuery(name = "Accountable.findByFreeUnitsApplied", query = "SELECT a FROM Accountable a WHERE a.freeUnitsApplied = :freeUnitsApplied")})
public class Accountable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "accountable_id")
    private Integer accountableId;
    @Basic(optional = false)
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @Column(name = "service")
    private String service;
    @Column(name = "destination")
    private String destination;
    @Column(name = "calee")
    private String calee;
    @Basic(optional = false)
    @Column(name = "period")
    private String period;
    @Basic(optional = false)
    @Column(name = "accounted_units")
    private long accountedUnits;
    @Basic(optional = false)
    @Column(name = "accounted_money")
    private long accountedMoney;
    @Basic(optional = false)
    @Column(name = "free_units_applied")
    private short freeUnitsApplied;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User user;

    public Accountable() {
    }

    public Accountable(Integer accountableId) {
        this.accountableId = accountableId;
    }

    public Accountable(Integer accountableId, Date date, String service, String period, long accountedUnits, long accountedMoney, short freeUnitsApplied) {
        this.accountableId = accountableId;
        this.date = date;
        this.service = service;
        this.period = period;
        this.accountedUnits = accountedUnits;
        this.accountedMoney = accountedMoney;
        this.freeUnitsApplied = freeUnitsApplied;
    }

    public Integer getAccountableId() {
        return accountableId;
    }

    public void setAccountableId(Integer accountableId) {
        this.accountableId = accountableId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCalee() {
        return calee;
    }

    public void setCalee(String calee) {
        this.calee = calee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getAccountedUnits() {
        return accountedUnits;
    }

    public void setAccountedUnits(long accountedUnits) {
        this.accountedUnits = accountedUnits;
    }

    public long getAccountedMoney() {
        return accountedMoney;
    }

    public void setAccountedMoney(long accountedMoney) {
        this.accountedMoney = accountedMoney;
    }

    public short getFreeUnitsApplied() {
        return freeUnitsApplied;
    }

    public void setFreeUnitsApplied(short freeUnitsApplied) {
        this.freeUnitsApplied = freeUnitsApplied;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountableId != null ? accountableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Accountable)) {
            return false;
        }
        Accountable other = (Accountable) object;
        if ((this.accountableId == null && other.accountableId != null) || (this.accountableId != null && !this.accountableId.equals(other.accountableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Accountable[accountableId=" + accountableId + "]";
    }

}
