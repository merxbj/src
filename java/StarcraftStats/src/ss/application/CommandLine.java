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

package ss.application;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandLine {

    private String dataFilePath;
    private boolean dumpData;
    private boolean quickStats;

    private CommandLine() {
        dataFilePath = "Starcraft2.xml";
        dumpData = false;
        quickStats = false;
    }

    public static CommandLine parse(String[] args) throws InvalidCommandLineException {
        CommandLine cl = new CommandLine();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-dumpdata")) {
                cl.dumpData = true;
            } else if (args[i].equals("-quickstats")) {
                cl.quickStats = true;
            } else if (args[i].startsWith("-")) {
                throw new InvalidCommandLineException(args[i]);
            }
        }
        cl.dataFilePath = args[args.length - 1]; // last parameter is always the data file
        return cl;
    }

    public static void handleInvalidInput(InvalidCommandLineException ex) {
        System.out.println(String.format("Command line usage has been claimed invalid: %s!", ex.getMessage()));
    }

    public static void handleInvalidDataFile(InvalidDataFileException ex) {
        System.out.println(String.format("Data file has been claimed invalid: %s!", ex.getMessage()));
    }

    public static void handleException(Throwable ex) {
        System.out.println(formatException(ex));
    }

    static void showHelp() {
        System.out.println("Usage: [switches] path_to_data_file");
        System.out.println("Switches:\n\t-datadump:\tDisplays dump of given data file");
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public boolean isDumpData() {
        return dumpData;
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    private static String formatException(Throwable ex) {
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
