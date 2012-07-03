package cz.cvut.fel.ad7b39wpa.core;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * Time interval represented by its start date and end date.
 * @author jmerxbauer
 */
public interface Interval extends Comparable<Interval>, Serializable {
    
    /**
     * Gets the start date part of this interval.
     * @return the start date
     */
    public DateTime getStartDate();
    
    /**
     * Sets the start date part of this interval.
     * @param startDate the start date
     */
    public void setStartDate(DateTime startDate);
    
    /**
     * Gets the end date part of this interval.
     * @return the end date
     */
    public DateTime getEndDate();
    
    /**
     * Sets the end date part of this interval.
     * @param endDate the end date
     */
    public void setEndDate(DateTime endDate);
}
