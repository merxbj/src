/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.Duration;

/**
 *
 * @author jm185267
 */
public class WeeklyReport {

    private Map<Integer, DayEntry> dayEntries;
    private Duration total;

    public WeeklyReport(Map<Integer, DayEntry> dayEntries, Duration total) {
        this.dayEntries = dayEntries;
        this.total = total;
    }

    public Map<Integer, DayEntry> getDayEntries() {
        return dayEntries;
    }

    public Duration getTotal() {
        return total;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        buildTaskList(builder);
        buildWeeklyReport(builder);        
        buildTotalHours(builder);
        return builder.toString();
    }

    private void buildWeeklyReport(StringBuilder builder) {
        for (DayEntry entry : dayEntries.values()) {
            builder.append(entry);
        }
    }

    private void buildTotalHours(StringBuilder builder) {
        builder.append(String.format("Total worked hours: %s", Common.getPeriodFormatter().print(total.toPeriod())));
        builder.append(System.lineSeparator());
    }

    private void buildTaskList(StringBuilder builder) {
        Map<Task, Task> uniqueTaskList = new HashMap<>();
        for (DayEntry de : dayEntries.values()) {
            for (TaskEntry te : de.getTaskEntries().values()) {
                uniqueTaskList.put(te.getTask(), te.getTask());
            }
        }

        List<Task> sortedTaskList = new ArrayList<>(uniqueTaskList.values());
        Collections.sort(sortedTaskList);
        
        builder.append("Trakced tasks list:");
        builder.append(System.lineSeparator());
        for (Task task : sortedTaskList) {
            builder.append(String.format("\t%s", task));
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
    }
}
