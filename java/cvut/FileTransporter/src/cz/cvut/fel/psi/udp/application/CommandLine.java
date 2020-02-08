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
package cz.cvut.fel.psi.udp.application;

import java.net.InetAddress;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class CommandLine {

    private static final int DEFAULT_SERVER_PORT = 3999;
    private static final String DEFAULT_DOWNLOAD_FILE_NAME = "foto.png";
    private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.Verboose;

    public static void printUsage() {
        System.out.println("\n\nUSAGE:");
        System.out.println("\n\tFileTransporter [OPTION]... HOSTNAME [FIRMWARE]");
        System.out.println("\nDESCRIPTION:");
        System.out.println("\tPsiTP4 initiates an UDP based connection with a robot");
        System.out.println("\tlocated on a distant planet in order to download a nice");
        System.out.println("\tsightseeing photo or to upload new firmware.");
        System.out.println("\nOPTIONS:");
        System.out.println("\n\t--port=PORT");
        System.out.println("\t\tExplicitely specify the remote PORT to which the robot");
        System.out.println("\t\tis listening. Otherwise the default value 3999 is assumed.");
        System.out.println("\n\t--download-file-name=FILE");
        System.out.println("\t\tExplicitely specify the local FILE name of downloadded photo.");
        System.out.println("\t\tOtherwise the default value photo.png is assumed.");
        System.out.println("\n\t--log-level=LEVEL");
        System.out.println("\t\tExplicitely specify this client logging LEVEL.");
        System.out.println("\t\tOtherwise the default value Verboose is assumed.");
        System.out.println("\n\t\t\t Verboose\tLogging everything.");
        System.out.println("\n\t\t\t Normal\t\tLogging excludes the in/out traffic.");
        System.out.println("\n\t\t\t Simple\t\tLogging excludes the in/out traffic");
        System.out.println("\t\t\t\t\tand the window slides.");
    }
    private InetAddress hostname;
    private String firmwareFileName;
    private int port;
    private LogLevel logLevel;
    private String downloadFileName;

    public static CommandLine parse(final String[] args) {
        CommandLine cl = new CommandLine();

        try {
            int argi = 0;
            while (argi < args.length) {
                if (args[argi].startsWith("--port")) {
                    cl.port = Integer.parseInt(args[argi].split("=")[1]);
                    System.out.printf("Overriding the default port with %d\n", cl.port);
                } else if (args[argi].startsWith("--download-file-name")) {
                    cl.downloadFileName = args[argi].split("=")[1];
                    System.out.printf("Overriding the default download file name with %s\n", cl.downloadFileName);
                } else if (args[argi].startsWith("--log-level")) {
                    cl.logLevel = LogLevel.fromString(args[argi].split("=")[1]);
                    System.out.printf("Overriding the default log level with %s\n", cl.logLevel.toString());
                } else if (args[argi].startsWith("--help")) {
                    throw new RuntimeException(); // just to quickly move ahead to print the usage
                } else if (args[argi].startsWith("-")) {
                    throw new RuntimeException(); // just to quickly move ahead to print the usage
                } else {
                    break; // no more options - go ahead with regular parameters
                }
                argi++;
            }

            cl.hostname = InetAddress.getByName(args[argi++]);
            cl.firmwareFileName = args.length == argi ? "" : args[argi];
        } catch (Exception ex) {
            cl = null;
        }

        return cl;
    }

    public CommandLine() {
        hostname = null;
        firmwareFileName = "";
        port = DEFAULT_SERVER_PORT;
        logLevel = DEFAULT_LOG_LEVEL;
        downloadFileName = DEFAULT_DOWNLOAD_FILE_NAME;
    }

    public final InetAddress getHostname() {
        return hostname;
    }

    public final String getFirmwareFileName() {
        return firmwareFileName;
    }

    public final LogLevel getLogLevel() {
        return logLevel;
    }

    public final int getPort() {
        return port;
    }

    public final String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    public static String formatException(final Throwable ex) {
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

    public enum LogLevel {

        Verboose, Normal, Simple;

        public static LogLevel fromString(final String rawLogLevel) {
            if (Verboose.toString().startsWith(rawLogLevel)) {
                return Verboose;
            } else if (Normal.toString().startsWith(rawLogLevel)) {
                return Normal;
            } else if (Simple.toString().startsWith(rawLogLevel)) {
                return Simple;
            }

            throw new RuntimeException(String.format("Incorrect logging level specification: %s", rawLogLevel));
        }
    }
}
