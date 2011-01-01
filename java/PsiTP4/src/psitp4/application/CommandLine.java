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

    private int port;
    private InetAddress hostname;
    private String remoteFileName;
    private String localFileName;

    public static CommandLine parse(String[] args) {
        CommandLine cl = new CommandLine();

        try {
            cl.setHostname(InetAddress.getByName(args[0]));
            cl.setPort(Integer.parseInt(args[1]));
            cl.setRemoteFileName(args[2]);
            cl.setLocalFileName(args[3]);
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

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

}
