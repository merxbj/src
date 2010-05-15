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
 * Class representing the input provided by the command line arguments. It allows
 * a simplified access to the parameters provided and a encapsulated way how to
 * parse them.
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandLine {

    private boolean debug;
    private boolean valid;
    private String logFile;

    private static final String defaultLogFile = "./log/notwa.log";

    /**
     * Let us hide the constructor. The new CommandLine instances are going to be
     * returned by {@link #parse(args)} call.
     */
    private CommandLine() {
        debug = false;
        valid = false;
        logFile = defaultLogFile;
    }

    /**
     * Parses the given array of <code>String</code>s usualy obtained as command
     * line arguments.
     *
     * @param args The arguments.
     * @return The new instance of <code>CommandLine</code> containing all the
     *         valid parsed command line arguments.
     */
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

    /**
     * Gets whether the user wants to obtain additional debugging information
     * through the debug window.
     *
     * @return  <code>true</code> if user wants to see the logging output in the
     *          window, <code>false</code> otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Gets whether the command line arguments were valid.
     *
     * @return  <code>true</code> if the arguments were valid, <code>false</code>
     *          otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the log file customized path.
     * 
     * @return  The path to the log file where the user want to store the logging
     *          information.
     */
    public String getLogFile() {
        return logFile;
    }
}
