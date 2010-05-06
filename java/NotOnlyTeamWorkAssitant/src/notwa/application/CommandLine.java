/*
 * CommandLine
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

package notwa.application;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandLine {

    private boolean debug;
    private boolean valid;
    private String logFile;

    private static final String defaultLogFile = "./log/notwa.log";

    private CommandLine() {
        debug = false;
        valid = false;
        logFile = defaultLogFile;
    }

    public static CommandLine parse(String[] args) {
        CommandLine cl = new CommandLine();

        for (String arg : args) {
            if (arg.equalsIgnoreCase("/debug")) {
                cl.debug = true;
            }
        }
        cl.valid = true;

        return cl;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isValid() {
        return valid;
    }

    public String getLogFile() {
        return logFile;
    }
}
