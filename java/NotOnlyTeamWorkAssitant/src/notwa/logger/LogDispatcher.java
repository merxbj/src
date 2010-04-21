/*
 * LogDispatcher
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.logger;

import java.util.Observable;
import java.util.Observer;
import notwa.logger.Logger.LogLevel;

/**
 * <code>Observable</code> logging helper allowing subscribing of several 
 * different <codee>Observers</code>. This allows to have several different
 * logging providers that could be invoked through a single call.
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class LogDispatcher extends Observable {

    /**
     * Registers a single logger, implementing the <code>Observer</code> interface.
     * This logger will then be notified about the logging event.
     * 
     * @param logger The actual logger to be registered.
     */
    public void registerLogger(Observer logger) {
        this.addObserver(logger);
    }

    /**
     * Logs a message with an error priority altogeather with additional parameters
     * for the message to be formated with.
     * 
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public void logError(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = FileLogger.LogLevel.LOG_LEVEL_ERROR;
        this.setChanged();

        this.notifyObservers();
    }

    /**
     * Logs a message with a debug priority altogeather with additional parameters
     * for the message to be formated with.
     * 
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public void logDebug(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = FileLogger.LogLevel.LOG_LEVEL_DEBUG;
        this.setChanged();

        this.notifyObservers();
    }

    /**
     * Logs a message with an info priority altogeather with additional parameters
     * for the message to be formated with.
     * 
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public void logInfo(String message, Object... args) {
        this.message = message;
        this.args = args;
        this.logLevel = FileLogger.LogLevel.LOG_LEVEL_INFO;
        this.setChanged();

        this.notifyObservers();
    }

    /**
     * Logs a message with an error priority.
     * 
     * @param message The message to be logged.
     */
    public void logError(String message) {
        logError(message, (Object []) null);
    }

    /**
     * Logs a message with a debug priority.
     *
     * @param message The message to be logged.
     */
    public void logDebug(String message) {
        logDebug(message, (Object []) null);
    }

    /**
     * Logs a message with an info priority.
     * 
     * @param message The message to be logged.
     */
    public void logInfo(String message) {
        logInfo(message, (Object []) null);
    }

    /**
     * Gets the arguements provided to be formated into the given log message.
     * 
     * @return The arguements.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Gets the message to be logged. If the {@link #getArgs()} returns not-null
     * value, it should be formated together with those arguments.
     * 
     * @return The format message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the actual logging level of the given message.
     *
     * @return The logging level.
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    private String message;
    private Object[] args;
    private FileLogger.LogLevel logLevel;
}
