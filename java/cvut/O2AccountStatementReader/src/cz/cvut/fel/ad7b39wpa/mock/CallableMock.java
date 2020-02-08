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
}