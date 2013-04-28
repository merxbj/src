package cz.merxbj.unip.core;

import org.joda.time.*;
import cz.merxbj.unip.common.CommonStatics;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author mrneo
 */
public class LogEvent implements Iterable<LogEvent> {

    private LogEvent parent;
    private String description;
    private LogEventType type = LogEventType.UNKNOWN;
    private LogEventInfoType infoType;
    private DateTime startTimeStamp;
    private DateTime endTimeStamp;
    private ArrayList<LogEvent> subEvents;

    public LogEvent() {
    };
    
    public LogEvent(String description) {
        this(description, null, null, null);
    }

    public LogEvent getParent() {
        return parent;
    }

    public void setParent(LogEvent parent) {
        this.parent = parent;
    }

    public LogEvent(String description, LogEventInfoType infoType, DateTime startTimeStamp, DateTime endTimeStamp) {
        this.description = description;
        this.infoType = infoType;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LogEventType getType() {
        return type;
    }

    public void setType(LogEventType type) {
        this.type = type;
    }

    public void setInfoType(LogEventInfoType type) {
        this.infoType = type;
    }

    public LogEventInfoType getInfoType() {
        return infoType;
    }

    public DateTime getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(DateTime endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public DateTime getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(DateTime startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void addChild(LogEvent child) {
        if (this.subEvents == null) {
            this.subEvents = new ArrayList<LogEvent>();
        }
        this.subEvents.add(child);
    }

    public boolean hasChild() {
        return subEvents != null;
    }

    public ArrayList<LogEvent> getChilds() {
        return subEvents;
    }

    @Override
    public String toString() {
        return this.description;
    }

    /**
     *
     * @return long event duration time in miliseconds
     */
    public long getDuration() {
        int duration = 0;
        
        if (this.getEndTimeStamp() != null && this.getStartTimeStamp() != null) {
            duration += (this.getEndTimeStamp().getMillis() - this.getStartTimeStamp().getMillis());
        }
        return duration;
    }

    @Override
    public Iterator<LogEvent> iterator() {
        return subEvents.iterator();
    }

    public void parseFromTxt(String line) {
        DateTimeFormatter formatter = CommonStatics.TIME_FORMATTER;
        this.description = line.substring(51).trim();
        this.type = LogEventType.parseFromDescription(description);
        this.infoType = LogEventInfoType.valueOf(line.substring(line.indexOf("[") + 1, line.indexOf("]")).trim().toUpperCase());
        try {
            this.startTimeStamp = (DateTime) formatter.parseDateTime(line.substring(22, 35).trim());
        } catch (Exception e) {
        }
    }
}
