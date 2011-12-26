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

package psitp4.application;

import java.net.InetAddress;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class CommandLine {

    private InetAddress hostname;
    private String firmwareFileName;

    public static CommandLine parse(String[] args) {
        CommandLine cl = new CommandLine();

        try {
            cl.setHostname(InetAddress.getByName(args[0]));
            cl.setFirmwareFileName(args[1]);
        } catch (Exception ex) {
            throw new RuntimeException("Client parameters are valid ip adress (DNS name), port number, remote file name and local file name!", ex);
        }

        return cl;
    }

    public InetAddress getHostname() {
        return hostname;
    }

    public void setHostname(InetAddress hostname) {
        this.hostname = hostname;
    }

    public String getFirmwareFileName() {
        return firmwareFileName;
    }

    public void setFirmwareFileName(String remoteFileName) {
        this.firmwareFileName = remoteFileName;
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    public static String formatException(Throwable ex) {
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
