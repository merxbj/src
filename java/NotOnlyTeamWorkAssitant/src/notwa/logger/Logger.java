/*
 * Logger
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

import java.util.Date;

/**
 * The base class for all loggers. It mandates implementation of methods performing
 * the core logging implementation.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Logger {
    
    /**
     * The exception handler for the exceptions thrown during logging. This is more
     * exceptional than any exception so let's have a exceptional member for such
     * purposes.
     */
    protected LoggingExceptionHandler leh;

    /**
     * The simple constructor doesn't providing the <code>LoggingExceptionHandler</code>.
     */
    protected Logger() {
        this.leh = null;
    }
    
    /**
     * The complex constructor providing the <code>LoggingExceptionHanlder</code>.
     * This handler is than used whenever the unexpected exceptional exception is
     * thrown during the logging.
     * <p>Please, be wise and do not use this <code>Logger</code> to act as
     * the <code>LoggingExceptionHandler</code>.</p>
     *
     * @param leh The actual <code>LoggingExceptionHandler</code>.
     */
    protected Logger(LoggingExceptionHandler leh) {
        this.leh = leh;
    }

    /**
     * Method notifying the <code>LoggingExceptionHandler</code> that the exception
     * state occurs and the <code>Exception</code> has been thrown during the 
     * logging process.
     * 
     * @param ex The exception to be handled.
     */
    protected void reportLoggingException(Exception ex) {
        if (leh != null) {
            leh.handle(ex);
        }
    }

    /**
     * Logs a message with an info priority.
     *
     * @param message The message to be logged.
     */
    public void info(String message)
    {
        info(message, (Object []) null);
    }

    /**
     * Logs a message with a debug priority.
     * 
     * @param message The message to be logged.
     */
    public void debug(String message) {
        debug(message, (Object []) null);
    }

     /**
     * Logs a message with an error priority.
     * 
     * @param message The message to be logged.
     */
    public void error(String message) {
        error(message, (Object []) null);
    }

    /**
     * Logs a message with an info priority altogeather with additional parameters
     * for the message to be formated with.
     * 
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public abstract void info(String message, Object... args);

    /**
     * Logs a message with a debug priority altogeather with additional parameters
     * for the message to be formated with.
     *
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public abstract void debug(String message, Object... args);

    /**
     * Logs a message with an error priority altogeather with additional parameters
     * for the message to be formated with.
     *
     * @param message The format string of the message to be logged.
     * @param args The arguments to be formated into the given message.
     */
    public abstract void error(String message, Object... args);

    /**
     * Formats the log message according to the given parameters to make the
     * different logging mechanisms to log uniformly.
     * 
     * @param level The log level under which the message logging has been demanded.
     * @param message The format string of the message to be logged.
     * @param args The args to be formmated into the given message.
     * @return The formatted message.
     */
    protected String formatMessage(LogLevel level, String message, Object... args) {
        String formattedMesage = String.format(message, args);
        Date dt = new Date();
        return String.format("%28s | %5s | %s\n", dt.toString(), level.toString(), formattedMesage);
    }

    /**
     * Describes the actual value of the message logged.
     */
    public enum LogLevel {

        /**
         * The message with this level is considered to inform about a bad
         * or exceptional behavior that must be investigated or reported as soon
         * as possible.
         */
        LOG_LEVEL_ERROR {
            @Override
            public String toString() {
                return "ERROR";
            }
        },

        /**
         * The message with this level is considered to inform about a usual
         * behavior that deserves to be reported.
         */
        LOG_LEVEL_INFO {
            @Override
            public String toString() {
                return "INFO";
            }
        },

        /**
         * The message with htis level is considered to be useful during the
         * debugging or for the problem-solving purposes.
         */
        LOG_LEVEL_DEBUG {
            @Override
            public String toString() {
                return "DEBUG";
            }
        };
    }
}
