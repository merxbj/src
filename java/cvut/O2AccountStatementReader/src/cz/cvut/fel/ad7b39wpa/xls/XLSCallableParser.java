/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Callable;

/**
 *
 * @author eTeR
 */
public class XLSCallableParser {
    
    private XLSCallableParser() {
    }

    public static Callable parse(String nonParsedCallable) {

        Callable cal = new Callable();

        int offset = 0;
        if (isInternationalNumber(nonParsedCallable)) {
            offset += 3;
            cal.setInternationalDialingCode(Integer.parseInt(nonParsedCallable.substring(0, offset)));
        } else if (isNormalNumber(nonParsedCallable)) {
            cal.setInternationalDialingCode(420);
        } else {
            cal.setSubscriberNumber(Integer.parseInt(nonParsedCallable)); // leave it as it is if we are unsure
            return cal;
        }

        cal.setDialingCode(Integer.parseInt(nonParsedCallable.substring(offset, offset+3)));
        cal.setSubscriberNumber(Integer.parseInt(nonParsedCallable.substring(offset+3, offset+9)));

        return cal;
    }

    private static boolean isInternationalNumber(String nonParsedCallable) {
        return nonParsedCallable.length() == 12;
    }

    private static boolean isNormalNumber(String nonParsedCallable) {
        return nonParsedCallable.length() == 9;
    }
}
