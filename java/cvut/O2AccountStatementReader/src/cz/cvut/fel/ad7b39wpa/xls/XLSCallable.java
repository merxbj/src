package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Callable;

public class XLSCallable implements Callable {

    private int internationalDialingCode;
    private int dialingCode;
    private long subscriberNumber;

    public XLSCallable() {
    }

    public XLSCallable(String nonParsedCallable) {
        this.parse(nonParsedCallable);
    }

    private void parse(String nonParsedCallable) {
        int offset = 0;
        if (isInternationalNumber(nonParsedCallable)) {
            offset += 3;
            this.setInternationalDialingCode(Integer.parseInt(nonParsedCallable.substring(0, offset)));
        } else if (!isNormalNumber(nonParsedCallable)) {
            this.setSubscriberNumber(Integer.parseInt(nonParsedCallable)); // leave it as it is if we are unsure
            return;
        }

        this.setInternationalDialingCode(420);
        this.setDialingCode(Integer.parseInt(nonParsedCallable.substring(offset, offset+3)));
        this.setSubscriberNumber(Integer.parseInt(nonParsedCallable.substring(offset+3, offset+9)));
    }

    private boolean isInternationalNumber(String nonParsedCallable) {
        return nonParsedCallable.length() == 12;
    }

    private boolean isNormalNumber(String nonParsedCallable) {
        return nonParsedCallable.length() == 9;
    }
    
    @Override
    public int getDialingCode() {
        return dialingCode;
    }

    @Override
    public void setDialingCode(int dialingCode) {
        this.dialingCode = dialingCode;
    }

    @Override
    public int getInternationalDialingCode() {
        return internationalDialingCode;
    }

    @Override
    public void setInternationalDialingCode(int internationalDialingCode) {
        this.internationalDialingCode = internationalDialingCode;
    }

    @Override
    public long getSubscriberNumber() {
        return subscriberNumber;
    }

    @Override
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
        final XLSCallable other = (XLSCallable) obj;
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