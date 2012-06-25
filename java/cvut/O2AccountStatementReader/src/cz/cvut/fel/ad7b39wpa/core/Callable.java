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
public class Callable implements Serializable {
    
    protected int internationalDialingCode;
    protected int dialingCode;
    protected long subscriberNumber;

    public Callable() {
        this(0,0,0l);
    }

    public Callable(int internationalDialingCode, int dialingCode, long subscriberNumber) {
        this.internationalDialingCode = internationalDialingCode;
        this.dialingCode = dialingCode;
        this.subscriberNumber = subscriberNumber;
    }

    /**
     * Gets the international dialing code part of this callable phone number.
     * @return the international dialing code
     */
    public int getInternationalDialingCode() {
        return internationalDialingCode;
    }

    /**
     * Sets the international dialing code part of this callable phone number.
     * @param internationalDialingCode the international dialing code
     */
    public void setInternationalDialingCode(int internationalDialingCode) {
        this.internationalDialingCode = internationalDialingCode;
    }

    /**
     * Gets the local dialing code part of this callable phone number.
     * @return the local dialing code
     */
    public int getDialingCode() {
        return this.dialingCode;
    }

    /**
     * Sets the local dialing code part of this callable phone number.
     * @param dialingCode the local dialing code
     */
    public void setDialingCode(int dialingCode) {
        this.dialingCode = dialingCode;
    }

    /**
     * Gets the subscriber number part of this callable phone number.
     * @return the phone number
     */
    public long getSubscriberNumber() {
        return this.subscriberNumber;
    }

    /**
     * Sets the subscriber number part of this callable phone number.
     * @param subscriberNumber the phone number
     */
    public void setSubscriberNumber(long subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Callable other = (Callable) obj;
        if (this.internationalDialingCode != other.internationalDialingCode) {
            return false;
        }
        if (this.dialingCode != other.dialingCode) {
            return false;
        }
        if (this.subscriberNumber != other.subscriberNumber) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.internationalDialingCode;
        hash = 83 * hash + this.dialingCode;
        hash = 83 * hash + (int) (this.subscriberNumber ^ (this.subscriberNumber >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "+" + internationalDialingCode + " " + dialingCode + " " + subscriberNumber;
    }
}
