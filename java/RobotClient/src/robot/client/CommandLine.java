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

package robot.client;

import java.net.InetAddress;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandLine {

    private int portNumber;
    private InetAddress address;

    public static CommandLine parse(String[] args) {
        CommandLine cl = new CommandLine();

        try {
            cl.setAddress(InetAddress.getByName(args[0]));
            cl.setPortNumber(Integer.parseInt(args[1]));
        } catch (Exception ex) {
            throw new RuntimeException("Client parameters are valid ip adress (DNS name) and port number <3500,3800>!", ex);
        }

        if (cl.getPortNumber() < 3500 || cl.getPortNumber() > 3800) {
            throw new RuntimeException("The port number must be in closed interval <3500,3800>!");
        }

        return cl;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

}
