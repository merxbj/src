/*
 * FileLogger
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

import notwa.exception.LoggingException;
import java.io.FileWriter;
import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * The <code>Logger</code> concrete implementation storing the logging material
 * into the file which name was either:
 * <ul>
 * <li>Application.log in the current working directory, or</li>
 * <li>Specified through the constructor</li>
 * </ul>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class FileLogger extends Logger implements Observer {

    private String fileName = "Application.log";

    /**
     * The simplest constructor delegating all the stuff to the base.
     */
    public FileLogger() {
        super();
    }

    /**
     * The constructor allowing the file name specification.
     *
     * @param fileName The actual file name where to log the given messages.
     */
    public FileLogger(String fileName) {
        super();
        this.fileName = fileName;
        buildDirectoryTree();
    }

    /**
     * The complex contructor allowing the file name and <code>LoggingExceptionHandler</code>
     * specification.
     *
     * @param fileName The actual file name where to log the given messages.
     * @param leh   The actual <code>LoggingExceptionHandler</code> where the
     *              <code>Exception</code> thrown during the logging should be
     *              reported.
     */
    public FileLogger(String fileName, LoggingExceptionHandler leh) {
        super(leh);
        this.fileName = fileName;
        buildDirectoryTree();
    }

    /**
     * Makes sure that the directory path where the log file is going to be stored
     * exists. If it doesn't, it will be created.
     */
    private void buildDirectoryTree() {
        try {
            File logFile = new File(fileName.substring(0, fileName.lastIndexOf("/")));
            logFile.mkdirs();
        } catch (Exception ex) {
            reportLoggingException(ex);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LogDispatcher) {
            LogDispatcher ld = (LogDispatcher) o;
            writeToLog(ld.getLogLevel(), ld.getMessage(), ld.getArgs());
        } else {
            reportLoggingException(new LoggingException("Invalid observable object got during observer update"));
        }
    }

    @Override
    public void info(String message, Object... args) {
        writeToLog(LogLevel.LOG_LEVEL_INFO, message, args);
    }

    @Override
    public void debug(String message, Object... args) {

        writeToLog(LogLevel.LOG_LEVEL_DEBUG, message, args);
    }

    @Override
    public void error(String message, Object... args) {
        writeToLog(LogLevel.LOG_LEVEL_ERROR, message, args);
    }

    /**
     * Performs the actual act of writing the provided message with formmating
     * arguments to the file altogether with the current timestamp, formatted in
     * the well readable manner.
     *
     * @param level The log level under which the message logging has been demanded.
     * @param message The message to be logged.
     * @param args The args to be formmated into the given message.
     */
    private void writeToLog(LogLevel level, String message, Object... args) {
        if (message == null)
            reportLoggingException(new LoggingException("Null message has been requested to be logged!"));

        FileWriter fw = null;
        try {
            String formattedMesage = String.format(message, args);
            fw = new FileWriter(fileName, true /* append */);
            Date dt = new Date();
            fw.append(String.format("%28s | %5s | %s\n", dt.toString(), level.toString(), formattedMesage));
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
}
