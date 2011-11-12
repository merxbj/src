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

package robot.server.logging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import robot.common.request.*;
import robot.common.response.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Logger {

    private String name;

    private static HashMap<String, Logger> loggers;
    static {
        loggers = new HashMap<String, Logger>();
    }

    public static synchronized Logger getLogger(String robot) {
        if (loggers.containsKey(robot)) {
            return loggers.get(robot);
        } else {
            Logger log = new Logger(robot);
            loggers.put(robot, log);
            return log;
        }
    }

    private Logger(String name) {
        this.name = name;
    }

    public void logRequest(Request request) {
        log(String.format("Received request %s addressed to %s!", request.getClass().getName(), request.getAdress()));
    }

    public void logResponse(Response response) {
        log(String.format("Sent response %s. This %s close the connection!", response.getClass().getName(), response.isEndGame() ? "will" : "will not"));
    }

    public void logException(Throwable exception) {
        log(formatException(exception));
    }

    public void logMessage(String message, Object... args) {
        log(String.format(message, args));
    }

    private void log(String message) {
        Date date = Calendar.getInstance().getTime();
        System.out.println(String.format("%25s | [%s] | %s", date, name, message));
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
            sb.append(formatException(innerException));
            sb.append("\n");
            innerException = innerException.getCause();
        }

        return sb.toString();
    }

}
