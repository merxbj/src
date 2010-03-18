package logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import logger.Logger.LogLevel;

public class LogDispatcher extends Observable
/*
 * Eternity's logger
 */
{

    public void registerLogger(Observer logger) {
        this.addObserver(logger);
    }

    public void logError(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = Logger.LogLevel.LOG_LEVEL_ERROR;
        this.setChanged();

        this.notifyObservers();
    }

    public void logWarning(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = Logger.LogLevel.LOG_LEVEL_DEBUG;
        this.setChanged();

        this.notifyObservers();
    }

    public void logInfo(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = Logger.LogLevel.LOG_LEVEL_INFO;
        this.setChanged();

        this.notifyObservers();
    }

    public void logError(String message) {
        logError(message, (Object []) null);
    }

    public void logWarning(String message) {
        logWarning(message, (Object []) null);
    }

    public void logInfo(String message) {
        logInfo(message, (Object []) null);
    }

    public Object[] getArgs() {
        return args;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    public String getFormattedMessage()
    {
    	String formattedMessage = null;
        try
        {
            String formatMessage = String.format(message, args);
            Date dt = new Date();
        	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); //time is enought i think
            formattedMessage = new String(String.format("%8s | %5s | %s\n",
            										dateFormat.format(dt),
            										Logger.logLevelToString(logLevel),
            										formatMessage));
        }
        catch (Exception ex)
        {
        	// i dont care
        }
        
        return formattedMessage;
    }
    
    private String message;
    private Object[] args;
    private Logger.LogLevel logLevel;
}
