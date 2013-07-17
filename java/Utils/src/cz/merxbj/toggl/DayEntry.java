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
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

/**
 *
 * @author jm185267
 */
public class DayEntry {

    private int dayOfWeek;
    private Map<Task, TaskEntry> taskEntries;

    public DayEntry(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.taskEntries = new HashMap<>();
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Map<Task, TaskEntry> getTaskEntries() {
        return taskEntries;
    }

    public void setTaskEntries(Map<Task, TaskEntry> taskEntries) {
        this.taskEntries = taskEntries;
    }
    
    public void addTimeEntry(TimeEntry te) {
        if (te.getStartDateTime().getDayOfWeek() != dayOfWeek) {
            throw new RuntimeException("Attempted to add duration from time entry of different day.");
        }
        TaskEntry entry = getTaskEntry(new Task(te.getUser(), te.getClient(), te.getProject(), te.getTask()));
        entry.addTimeEntry(te);
    }

    private TaskEntry getTaskEntry(Task forTask) {
        TaskEntry entry;
        if (taskEntries.containsKey(forTask)) {
            entry = taskEntries.get(forTask);
        } else {
            entry = new TaskEntry(forTask, Duration.ZERO);
            taskEntries.put(forTask, entry);
        }
        return entry;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        buildDayHeader(builder);
        buildTaskEntries(builder);
        buildDayTotal(builder);
        return builder.toString();
    }

    private String dayOfWeekToString(Integer dayOfWeek) {
        switch (dayOfWeek) {
            case DateTimeConstants.MONDAY:
                return "Monday";
            case DateTimeConstants.TUESDAY:
                return "Tuesday";
            case DateTimeConstants.WEDNESDAY:
                return "Wednesday";
            case DateTimeConstants.THURSDAY:
                return "Thursday";
            case DateTimeConstants.FRIDAY:
                return "Friday";
            case DateTimeConstants.SATURDAY:
                return "Saturday";
            case DateTimeConstants.SUNDAY:
                return "Sunday";
            default:
                throw new RuntimeException("Unknown day of week: " + dayOfWeek);
        }
    }

    private void buildDayHeader(StringBuilder builder) {
        String dayOfWeekString = dayOfWeekToString(dayOfWeek);
        builder.append(String.format("%s:",dayOfWeekString));
        builder.append(System.lineSeparator());
    }

    private void buildTaskEntries(StringBuilder builder) {
        List<TaskEntry> sortedTaskEntries = new ArrayList<>(taskEntries.values());
        Collections.sort(sortedTaskEntries);
        
        for (TaskEntry te : sortedTaskEntries) {
            builder.append(String.format("\t%s", te));
            builder.append(System.lineSeparator());
        }
    }

    private void buildDayTotal(StringBuilder builder) {
        Duration total = Duration.ZERO;
        for (TaskEntry te : taskEntries.values()) {
            total = total.plus(te.getDuration());
        }
        builder.append(System.lineSeparator());
        builder.append(String.format("\t%100s\t%s", "Total worked hours this day:", Common.getPeriodFormatter().print(total.toPeriod())));
        builder.append(System.lineSeparator());
    }
}
