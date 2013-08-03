/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.Duration;

/**
 *
 * @author jm185267
 */
public class WeeklyReportGenerator implements ReportGenerator<WeeklyReport> {

    public WeeklyReportGenerator() {
    }

    @Override
    public WeeklyReport generate(List<TimeEntry> entries) {
        
        Map<Integer, DayEntry> dayEntries = new HashMap<>();
        Duration total = Duration.ZERO;
        
        for (TimeEntry te : entries) {
            DayEntry de = getDayEntry(dayEntries, te.getStartDateTime().getDayOfWeek());
            de.addTimeEntry(te);
            total = total.plus(te.calculateDuration());
        }
        
        return new WeeklyReport(dayEntries, total);
    }

    private DayEntry getDayEntry(Map<Integer, DayEntry> dayEntries, int dayOfWeek) {
        DayEntry de;
        if (dayEntries.containsKey(dayOfWeek)) {
            de = dayEntries.get(dayOfWeek);
        } else {
            de = new DayEntry(dayOfWeek);
            dayEntries.put(dayOfWeek, de);
        }
        return de;
    }
    
}
