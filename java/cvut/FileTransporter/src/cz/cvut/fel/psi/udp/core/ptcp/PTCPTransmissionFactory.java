/*
 * ConnectionFactory
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
package cz.cvut.fel.psi.udp.core.ptcp;

import cz.cvut.fel.psi.udp.core.Connection;
import cz.cvut.fel.psi.udp.core.TransmissionFactory;
import cz.cvut.fel.psi.udp.statemachine.TransmissionStateMachine;
import cz.cvut.fel.psi.udp.statemachine.ptcp.PTCPTransmissionStateMachine;
import java.net.InetAddress;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class PTCPTransmissionFactory implements TransmissionFactory {

    public PTCPTransmissionFactory() {
    }

    public Connection newConnection(InetAddress hostname, int port) {
        return new PTCPConnection(hostname, port);
    }

    public TransmissionStateMachine newTransmissionStateMachine(Connection connection) {
        PTCPConnection con = (PTCPConnection) connection;
        if (con == null) {
            throw new RuntimeException("Invalid Connection supplied. Expected PTCPConnection. Programmer error?");
        }
        return new PTCPTransmissionStateMachine(con);
    }
}
