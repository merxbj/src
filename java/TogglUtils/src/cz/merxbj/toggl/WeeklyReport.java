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
        buildTaskReport(builder);
        buildDayReport(builder);        
        buildTotalHours(builder);
        return builder.toString();
    }

    private void buildDayReport(StringBuilder builder) {
        builder.append("=============== DAY REPORT ===============");
        builder.append(System.lineSeparator());
        for (DayEntry entry : dayEntries.values()) {
            builder.append(entry);
        }
    }

    private void buildTotalHours(StringBuilder builder) {
        builder.append(String.format("Total worked hours: %s", Common.getPeriodFormatter().print(total.toPeriod())));
        builder.append(System.lineSeparator());
    }

    private void buildTaskReport(StringBuilder builder) {
        Map<Task, Task> uniqueTaskList = createUniqueTaskList();

        List<Task> sortedTaskList = new ArrayList<>(uniqueTaskList.values());
        Collections.sort(sortedTaskList);

        builder.append("=============== TASK REPORT ===============");
        builder.append(System.lineSeparator());

        builder.append(System.lineSeparator());
        for (Task task : sortedTaskList) {
            builder.append(String.format("%s:", task));
            builder.append(System.lineSeparator());
            buildTaskDayReport(builder, task);
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
    }

    private Map<Task, Task> createUniqueTaskList() {
        Map<Task, Task> uniqueTaskList = new HashMap<>();
        for (DayEntry de : dayEntries.values()) {
            for (TaskEntry te : de.getTaskEntries().values()) {
                uniqueTaskList.put(te.getTask(), te.getTask());
            }
        }
        return uniqueTaskList;
    }

    private void buildTaskDayReport(StringBuilder builder, Task task) {
        Duration taskTotal = Duration.ZERO;
        for (DayEntry de : dayEntries.values()) {
            if (de.getTaskEntries().containsKey(task)) {
                TaskEntry te = de.getTaskEntries().get(task);
                builder.append(String.format("%60s\t%s", de.dayOfWeekToString(), Common.getPeriodFormatter().print(te.getDuration().toPeriod())));
                builder.append(System.lineSeparator());

                taskTotal = taskTotal.plus(te.getDuration());
            }
        }

        builder.append(System.lineSeparator());
        builder.append(String.format("%60s\t%s", "Total worked hours on this task", Common.getPeriodFormatter().print(taskTotal.toPeriod())));
        builder.append(System.lineSeparator());
    }
}
