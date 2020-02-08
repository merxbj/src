/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.unip.core;

import cz.merxbj.unip.common.CommonStatics;
import java.awt.Color;

/**
 *
 * @author mrneo
 */
public enum LogEventType {

    BATCH, TASK, RECORD, DATABASE, UNKNOWN;
    public static final String BATCH_START = ">>STARTS LOAD BATCH";
    public static final String BATCH_CLOSING_START = "STARTS CLOSING BATCH TASK";
    public static final String TASK_START = "STARTS TASK";
    public static final String TASK_END = "<<ENDS CLOSE TASK";
    public static final String TASK_LOAD_END = "ENDS LOAD TASK";
    public static final String RECORD_START = "STARTS RECORD";
    public static final String DATABASE_OPEN = "OPEN DB TABLE";
    public static final String DATABASE_CLOSED = "CLOSED DATABASE";

    public static Color getColor(LogEvent event) {
        Color infoTypeColor = LogEventInfoType.getColor(event.getInfoType());
        if (infoTypeColor.equals(CommonStatics.DEFAULT_BACKGROUND)) {
            return getColor(event.getType());
        } else {
            return infoTypeColor;
        }
    }

    public static Color getColor(LogEventType type) {
        switch (type) {
            case BATCH:
                return CommonStatics.DARK_GREEN;
            case TASK:
                return CommonStatics.GREEN;
            case RECORD:
                return CommonStatics.LIGHT_GREEN;
            case DATABASE:
                return CommonStatics.LIGHT_BLUE;
            default:
                return CommonStatics.DEFAULT_BACKGROUND;
        }
    }

    public static LogEventType parseFromDescription(String line) {
        String upString = line.toUpperCase();
        if (upString.startsWith(TASK_START)) {
            return TASK;
        } else if (upString.startsWith(RECORD_START)) {
            return RECORD;
        } else if ((upString.startsWith(DATABASE_OPEN)) || (upString.startsWith(DATABASE_CLOSED))) {
            return DATABASE;
        } else if (upString.startsWith(BATCH_START)) {
            return BATCH;
        } else {
            return UNKNOWN;
        }
    }
}
