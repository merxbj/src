package cz.cvut.fel.ad7b39wpa.core;

import java.io.Serializable;

/**
 * Represents a special accountable period according to which the prices might be
 * adjusted.
 * @author jmerxbauer
 */
public enum AccountablePeriod implements Serializable {

    /**
     * This feature is not applicable to the service being accounted.
     */
    NOT_APPLICABLE,
    
    /**
     * The service being accounted is always accounted to a single period
     */
    ALWAYS,
    
    /**
     * The accountable event is accounted to a peak hours period
     */
    WITHIN_PEAK,
    
    /**
     * The accountable event is accounted to a outside peek hours period
     */
    OUTISDE_PEAK,
    
    /**
     * The accountable event is accounted to a special outside peek horus period
     * known as weekend.
     */
    WEEKEND,
    
    /**
     * The error condition where the accountable service is a subject of period
     * categorization but the period could not be determined.
     */
    //UNKNOWN
}
