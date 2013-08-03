/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 *
 * @author jm185267
 */
public class Common {
    private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours().appendLiteral(":").printZeroAlways().appendMinutes().appendLiteral(":").printZeroAlways().appendSeconds().toFormatter();
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.getDefault());
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(DateTimeZone.getDefault());

    public static PeriodFormatter getPeriodFormatter() {
        return periodFormatter;
    }

    public static DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public static DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }
}
