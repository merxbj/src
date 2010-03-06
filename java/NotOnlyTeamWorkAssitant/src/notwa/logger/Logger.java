package notwa.logger;

import java.io.FileWriter;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class Logger implements Observer {

    public Logger() {
    }

    public Logger(String fileName, String moduleName) {
        this.fileName = fileName;
        this.moduleName = moduleName;
    }

    public Logger(String fileName, String moduleName, LoggingExceptionHandler leh) {
        this.fileName = fileName;
        this.moduleName = moduleName;
        this.leh = leh;
    }

    public void update(Observable o, Object arg) {
        if (o instanceof LogDispatcher) {
            LogDispatcher ld = (LogDispatcher) o;
            writeToLog(ld.getLogLevel(), ld.getMessage(), ld.getArgs());
        } else {
            reportLoggingException(new LoggingException("Invalid observable object got during observer update"));
        }
    }

    public void info(String message, Object... args) {
        writeToLog(LogLevel.LOG_LEVEL_INFO, message, args);
    }

    public void debug(String message, Object... args) {

        writeToLog(LogLevel.LOG_LEVEL_DEBUG, message, args);
    }

    public void error(String message, Object... args) {
        writeToLog(LogLevel.LOG_LEVEL_ERROR, message, args);
    }

    public void info(String message) {
        info(message, (Object []) null);
    }

    public void debug(String message) {
        debug(message, (Object []) null);
    }

    public void error(String message) {
        error(message, (Object []) null);
    }

    private void writeToLog(LogLevel level, String message, Object... args) {
        if (message == null)
            reportLoggingException(new LoggingException("Null message has been requested to be logged!"));

        FileWriter fw = null;
        try {
            String formattedMesage = String.format(message, args);
            fw = new FileWriter(fileName, true /* append */);
            Date dt = new Date();
            fw.append(String.format("%28s | %5s | %s\n", dt.toString(), logLevelToString(level), formattedMesage));
        } catch (Exception ex) {
            // Exception caught during writing to log occurred
            reportLoggingException(ex);
        } finally {
            try {
                fw.close();
            } catch (Exception ex) {
                reportLoggingException(ex);
            }
        }
    }

    private String logLevelToString(LogLevel level) throws LoggingException {
        switch (level) {
            case LOG_LEVEL_DEBUG:
                return "DEBUG";
            case LOG_LEVEL_ERROR:
                return "ERROR";
            case LOG_LEVEL_INFO:
                return "INFO";
            default:
                throw new LoggingException("Unexpected logging level!");
        }
    }

    private void reportLoggingException(Exception ex) {
        if (leh != null) {
            leh.loggingExceptionOccured(ex);
        }
    }

    private String fileName = "Application.log";
    private String moduleName = "Default module";
    private LoggingExceptionHandler leh;

    public enum LogLevel {
        LOG_LEVEL_ERROR, LOG_LEVEL_INFO, LOG_LEVEL_DEBUG
    }
}
