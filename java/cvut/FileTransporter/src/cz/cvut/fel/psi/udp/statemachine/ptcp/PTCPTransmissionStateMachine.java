/*
 * StateMachine
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
package cz.cvut.fel.psi.udp.statemachine.ptcp;

import cz.cvut.fel.psi.udp.statemachine.*;

import cz.cvut.fel.psi.udp.core.ptcp.PTCPConnection;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPConnectionType;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PTCPTransmissionStateMachine extends TransmissionStateMachine {

    protected PTCPConnection connection;

    public PTCPTransmissionStateMachine(PTCPConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    public boolean configureDownload(String localFileName) {
        connection.setConnectionType(PTCPConnectionType.DOWNLOAD);
        firstState = new WaitingForConnectionState(new FileDownloadState(localFileName));

        return true;
    }

    @Override
    public boolean configureUpload(String firmwareFileName) {
        connection.setConnectionType(PTCPConnectionType.UPLOAD);
        firstState = new WaitingForConnectionState(new FileUploadState(firmwareFileName));

        return true;
    }
}
