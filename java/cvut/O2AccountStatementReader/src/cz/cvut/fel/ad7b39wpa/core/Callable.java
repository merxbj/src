package cz.cvut.fel.ad7b39wpa.core;

import java.io.Serializable;

/**
 * Represents a callable phone number in an international fashion.
 * Example of such number might be +420123456789, where:
 *      420 is an international dialing code
 *      123 is a local dialing code
 *      456789 is the actual subscriber number
 * @author jmerxbauer
 */
public interface Callable extends Serializable {
    
    /**
     * Gets the international dialing code part of this callable phone number.
     * @return the international dialing code
     */
    public int getInternationalDialingCode();

    /**
     * Sets the international dialing code part of this callable phone number.
     * @param internationalDialingCode the international dialing code
     */
    public void setInternationalDialingCode(int internationalDialingCode);

    /**
     * Gets the local dialing code part of this callable phone number.
     * @return the local dialing code
     */
    public int getDialingCode();

    /**
     * Sets the local dialing code part of this callable phone number.
     * @param dialingCode the local dialing code
     */
    public void setDialingCode(int dialingCode);

    /**
     * Gets the subscriber number part of this callable phone number.
     * @return the phone number
     */
    public int getSubscriberNumber();

    /**
     * Sets the subscriber number part of this callable phone number.
     * @param subscriberNumber the phone number
     */
    public void setSubscriberNumber(int subscriberNumber);
}
