package cz.cvut.fel.ad7b39wpa.core;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

/**
 * Represents a single accountable unit that is a part of the account statement.
 * @author jmerxbauer
 */
public interface Accountable {
    
    /**
     * Gets the date when the accountable event occurred.
     * @return the date
     */
    public Date getDate();
    
    /**
     * Sets the date when the accountable event occurred.
     * @param date the date
     */
    public void setDate(Date date);
    
    /**
     * Gets the time when the accountable event occurred within the boundaries of
     * the date.
     * @return the time
     */
    public Time getTime();
    
    /**
     * Sets the time when the accountable event occurred within the boundaries of
     * the date.
     * @param time the time
     */
    public void setTime(Time time);
    
    /**
     * Gets the service by which means the accountable event occurred.
     * @return the service
     */
    public ServiceType getService();
    
    /**
     * Sets the service by which means the accountable event occurred.
     * @param serviceType the service
     */
    public void setService(ServiceType serviceType);
    
    /**
     * Gets the not-structured destination of the accountable event.
     * @return the destination
     */
    public String getDestination();
    
    /**
     * Sets the not-structured destination of the accountable event.
     * @param destination the destination
     */
    public void setDestination(String destination);
    
    /**
     * Gets the callee (if applicable) of this accountable event. Otherwise
     * returns null.
     * @return the callee
     */
    public Callable getCallee();
    
    /**
     * Sets the callee (if applicable) of this accountable event.
     * @param callable the callee
     */
    public void setCallee(Callable callable);
    
    /**
     * Gets the accountable period the accountable event falls into.
     * @return the accountable period
     */
    public AccountablePeriod getAccountablePeriod();
    
    /**
     * Sets the accountable period the accountable event falls into.
     * @param accountablePeriod the accountable period
     */
    public void setAccountablePeriod(AccountablePeriod accountablePeriod);
    
    /**
     * Gets the accounted units measured for this accountable event. It might be:
     *      Time measure in seconds
     *      Data measure in bytes
     *      Count measure in units
     * @return accounted units
     */
    public long getAccountedUnits();
    
    /**
     * Sets the accounted units measured for this accountable event. It might be:
     *      Time measure in seconds
     *      Data measure in bytes
     *      Count measure in units
     * @param accountedUnits accounted units
     */
    public void setAccountedUnits(long accountedUnits);
    
    /**
     * Gets the money accounted for this accountable event.
     * @return the money
     */
    public BigInteger getAccountedMoney();

    /**
     * Sets the money accounted for this accountable event.
     * @param accountedMoney the money
     */
    public void setAccountedMoney(BigInteger accountedMoney);
    
    /**
     * Indicates the free units application on this accountable event.
     * @return true, if free units for that accountable event have been applied,
     * false otherwise.
     */
    public boolean getFreeUnitsApplied();
    
    /**
     * Indicates the free units application on this accountable event.
     * @param freeUnitsApplied true, if free units for that accountable event 
     * have been applied, false otherwise.
     */
    public void setFreeUnitsApplied(boolean freeUnitsApplied);
}
