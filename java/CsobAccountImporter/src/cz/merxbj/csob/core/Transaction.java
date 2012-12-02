/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import java.math.BigDecimal;
import org.joda.time.DateTime;

/**
 *
 * @author merxbj
 */
public class Transaction {
    
    private DateTime date;
    private BigDecimal amount;
    private String currency;
    private String ks;
    private String vs;
    private String ss;
    private Type type;
    private String offsetAccountName;
    private String offsetAccountNumber;
    private String comment;

    public Transaction() {
    }

    public enum Type {
        Card, 
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getKs() {
        return ks;
    }

    public void setKs(String ks) {
        this.ks = ks;
    }

    public String getOffsetAccountName() {
        return offsetAccountName;
    }

    public void setOffsetAccountName(String offsetAccountName) {
        this.offsetAccountName = offsetAccountName;
    }

    public String getOffsetAccountNumber() {
        return offsetAccountNumber;
    }

    public void setOffsetAccountNumber(String offsetAccountNumber) {
        this.offsetAccountNumber = offsetAccountNumber;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getVs() {
        return vs;
    }

    public void setVs(String vs) {
        this.vs = vs;
    }

    
    
}
