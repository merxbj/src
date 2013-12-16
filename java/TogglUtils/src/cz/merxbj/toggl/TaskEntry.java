/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.Objects;
import org.joda.time.Duration;

/**
 *
 * @author jm185267
 */
public class TaskEntry implements Comparable<TaskEntry> {
    private Task task;
    private Duration duration;

    TaskEntry(Task task, Duration duration) {
        this.task = task;
        this.duration = duration;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    public void addTimeEntry(TimeEntry te) {
        Task temp = new Task(te.getUser(), te.getClient(), te.getProject(), te.getTask());
        if (!task.equals(temp)) {
            throw new RuntimeException("Attempted to add duration from time entry of different task.");
        }
        this.duration = this.duration.plus(te.calculateDuration());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.task);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaskEntry other = (TaskEntry) obj;
        if (!Objects.equals(this.task, other.task)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(TaskEntry o) {
        if (o == null) {
            return 1;
        }
        return this.getTask().compareTo(o.getTask());
    }

    @Override
    public String toString() {
        return String.format("%60s:\t%s", task, Common.getPeriodFormatter().print(duration.toPeriod()));
    }
}
