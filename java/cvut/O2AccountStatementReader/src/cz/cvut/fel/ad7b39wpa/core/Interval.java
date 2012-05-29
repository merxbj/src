package cz.cvut.fel.ad7b39wpa.core;

import java.util.Date;

/**
 * Time interval represented by its start date and end date.
 * @author jmerxbauer
 */
public interface Interval {
    
    /**
     * Gets the start date part of this interval.
     * @return the start date
     */
    public Date getStartDate();
    
    /**
     * Sets the start date part of this interval.
     * @param startDate the start date
     */
    public void setStartDate(Date startDate);
    
    /**
     * Gets the end date part of this interval.
     * @return the end date
     */
    public Date getEndDate();
    
    /**
     * Sets the end date part of this interval.
     * @param endDate the end date
     */
    public void setEndDate(Date endDate);
}
