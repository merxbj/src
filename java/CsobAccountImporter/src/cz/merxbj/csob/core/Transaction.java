/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import java.math.BigDecimal;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author merxbj
 */
public class Transaction {

    private static final String DATE_TIME_PATTERN = "d.M.Y";
    
    private DateTime date;
    private BigDecimal amount;
    private String currency;
    private String ks;
    private String vs;
    private String ss;
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVs() {
        return vs;
    }

    public void setVs(String vs) {
        this.vs = vs;
    }

    void parseNvps(Map<String, String> nvps) {
        setDate(DateTime.parse(nvps.get(NVP.DATE), DateTimeFormat.forPattern(DATE_TIME_PATTERN)));
        setAmount(new BigDecimal(nvps.get(NVP.AMOUNT)));
        setCurrency(nvps.get(NVP.CURENCY));
        setKs(nvps.get(NVP.KS));
        setVs(nvps.get(NVP.VS));
        setSs(nvps.get(NVP.SS));
        setType(nvps.get(NVP.TYPE));
        setOffsetAccountName(nvps.get(NVP.OFFSET_ACCOUNT_NAME));
        setOffsetAccountNumber(nvps.get(NVP.OFFSET_ACCOUNT_NUMBER));
        setComment(nvps.get(NVP.COMMENT));
    }

    @Override
    public String toString() {
        return "Transaction{" + "date=" + date + "amount=" + amount + "currency=" + currency + "ks=" + ks + "vs=" + vs + "ss=" + ss + "type=" + type + "offsetAccountName=" + offsetAccountName + "offsetAccountNumber=" + offsetAccountNumber + "comment=" + comment + '}';
    }

    private static class NVP {
        private static final String DATE = "datum zaúčtování";
        private static final String AMOUNT = "částka";
        private static final String CURENCY = "měna";
        private static final String KS = "konstantní symbol";
        private static final String VS = "variabilní symbol";
        private static final String SS = "specifický symbol";
        private static final String TYPE = "označení operace";
        private static final String OFFSET_ACCOUNT_NAME = "název protiúčtu";
        private static final String OFFSET_ACCOUNT_NUMBER = "protiúčet";
        private static final String COMMENT = "poznámka";
    }

}
