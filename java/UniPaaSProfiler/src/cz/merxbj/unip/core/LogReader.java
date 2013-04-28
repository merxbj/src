package cz.merxbj.unip.core;

import cz.merxbj.unip.application.MainTask;
import cz.merxbj.unip.common.CommonStatics;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author mrneo
 */
public class LogReader {

    private boolean stop;
    private int totalRead = 0;
    private MainTask observer;
    private static final DateTimeFormatter timeFormatter = CommonStatics.TIME_FORMATTER;

    public synchronized void read(InputStream inputStream, LogEvent root) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        LogEvent latestStartEvent = root;
        LogEvent latestEvent = root;
        LogEvent newEvent = new LogEvent();
        while (((line = br.readLine()) != null) && !stop) {
            if (!line.startsWith(" ***")) { // lines marked like this should be skipped
                String textPart = line.substring(51).trim().toUpperCase();
                if ((textPart.startsWith("ENDS") && !textPart.startsWith(LogEventType.TASK_LOAD_END)) || textPart.startsWith(LogEventType.TASK_END)) {
                    try {
                        latestStartEvent.setEndTimeStamp((DateTime) timeFormatter.parseDateTime(line.substring(22, 35).trim()));
                        if (latestStartEvent.getParent() != null) {
                            latestStartEvent = latestStartEvent.getParent();
                        }
                    } catch (Exception e) {
                    }
                } else if ((textPart.startsWith("STARTS") || textPart.startsWith(LogEventType.BATCH_START)) && !textPart.startsWith(LogEventType.BATCH_CLOSING_START)) {
                    newEvent = new LogEvent();
                    newEvent.parseFromTxt(line);
                    newEvent.setParent(latestStartEvent);
                    latestStartEvent.addChild(newEvent);
                    latestStartEvent = newEvent;
                } else {
                    newEvent = new LogEvent();
                    newEvent.parseFromTxt(line);
                    latestStartEvent.addChild(newEvent);
                }
                latestEvent.setEndTimeStamp(newEvent.getStartTimeStamp());
                latestEvent = newEvent;

                updateProgress(line.getBytes().length);
            }
        }
    }

    private void updateProgress(int lenght) {
        totalRead += lenght + 2; // line lenght + 2, where 2 represents CR LF specials !
        observer.invokeUpdateProgress();
    }

    public void fireStop() {
        this.stop = true;
    }

    public int getCurrentTotal() {
        return totalRead;
    }

    public void registerObserver(MainTask observer) {
        this.observer = observer;
    }
}
