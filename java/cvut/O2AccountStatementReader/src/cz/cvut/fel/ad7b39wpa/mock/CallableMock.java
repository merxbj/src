package cz.cvut.fel.ad7b39wpa.mock;

import cz.cvut.fel.ad7b39wpa.core.Callable;
import java.util.Calendar;
import java.util.Random;

public class CallableMock extends Callable {

    private static Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private static int[] knownDialingCodes = {602, 728, 724, 728, 603, 608, 723};

    public static Callable createRandomCallable() {
        Callable cal = new CallableMock();
        cal.setInternationalDialingCode(420);
        cal.setDialingCode(knownDialingCodes[random.nextInt(knownDialingCodes.length)]);
        cal.setSubscriberNumber(100000 + (Math.abs(random.nextLong()) % (999999-100000)));
        return cal;
    }

    public CallableMock() {
        this(0, 0, 0);
    }

    public CallableMock(int internationalDialingCode, int dialingCode, long subscriberNumber) {
        this.internationalDialingCode = internationalDialingCode;
        this.dialingCode = dialingCode;
        this.subscriberNumber = subscriberNumber;
    }

    public int getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(int dialingCode) {
        this.dialingCode = dialingCode;
    }

    public int getInternationalDialingCode() {
        return internationalDialingCode;
    }

    public void setInternationalDialingCode(int internationalDialingCode) {
        this.internationalDialingCode = internationalDialingCode;
    }

    public long getSubscriberNumber() {
        return subscriberNumber;
    }

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
        final CallableMock other = (CallableMock) obj;
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