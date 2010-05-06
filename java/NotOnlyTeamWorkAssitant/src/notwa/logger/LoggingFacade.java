/*
 * LoggingFacade
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

/**
 * The class, implemented as a singleton, providing an unified access to the
 * logger and moreover the unified way how to report exceptions.
 * <p>You can either obtain the instance of this interface and then find the method
 * you need, or, to be more comfortable, the logger getter has been pulled out
 * as a static method to save one additional call</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class LoggingFacade {
    
    private static LoggingFacade singleton;
    private LogDispatcher ld;
    
    /**
     * Gets the one and only instance of this class.
     *
     * @return The actuall instance of this class.
     */
    public static LoggingFacade getInstance() {
        if (singleton == null) {
            singleton = new LoggingFacade();
        }
        return singleton;
    }
    
    /**
     * The hidden constructor meant to be used only from within this class.
     */
    protected LoggingFacade() {
        ld = new LogDispatcher();
    }

    /**
     * Turns on the logging into the file represented as a path.
     * 
     * @param fileName The file to be logged into.
     */
    public void enableFileLogging(String fileName) {
        ld.registerLogger(new FileLogger(fileName));
    }

    /**
     * Turns on the logging into the debug window.
     */
    public void enableWindowLogging() {
        ld.registerLogger(new WindowLogger());
    }

    /**
     * Gets the logger instance maintained within the signleton instance of this
     * class.
     * <p>The logger provides you with several methods allowing you to report
     * errors, warnings and infos all three with version with/out string formatting
     * support.</p>
     * 
     * @return The actuall logger.
     */
    public static LogDispatcher getLogger() {
        return getInstance().ld;
    }
    
    /**
     * Repors the caught <code>Exception</code> in a unified way. This means that 
     * it logs the error message with string representation of the given exception.
     * This is followed by the stack trace and finally by the inner exception 
     * which caused the given exception.
     *
     * @param ex <code>Exception<code> to be handled.
     */
    public static void handleException(Throwable ex) {
        LoggingFacade lf = getInstance();
        lf.ld.logError(lf.formatException(ex));
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    private String formatException(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString());
        sb.append("\n");
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\t at ");
            sb.append(ste.toString());
            sb.append("\n");
        }

        Throwable innerException = ex.getCause();
        while (innerException != null) {
            sb.append("\t caused by ");
            sb.append(innerException.toString());
            sb.append("\n");
            innerException = innerException.getCause();
        }

        return sb.toString();
    }
    
}
