/*
 * FileTransmissionState
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPConnectionResetException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPFlag;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPPacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPResponsePacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPInboundSlidingWindow;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPProtocolException;
import cz.cvut.fel.psi.udp.statemachine.Context;
import cz.cvut.fel.psi.udp.statemachine.StateTransitionStatus;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class FileDownloadState extends PTCPState {

    private String localFileName;

    public FileDownloadState(String localFileName) {
        this.localFileName = localFileName;
    }

    public StateTransitionStatus process(Context context) {
        try {

            PTCPInboundSlidingWindow window = new PTCPInboundSlidingWindow(connection.getSequence());

            PTCPPacket received = connection.receive();
            while (received.getFlag() != PTCPFlag.FIN) {

                checkFlags(received.getFlag());

                if (window.accept(received.getData(), received.getSeq())) {
                    window.slideWindow();
                }

                PTCPPacket response = new PTCPResponsePacket(window.getBegin());
                response.setSeq(received.getAck());
                connection.send(response);

                received = connection.receive();
            }

            long size = saveFile(window.getData(), localFileName);
            return context.doStateTransition(new RemoteSideDisconnectedState());

        } catch (PTCPProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            try {
                connection.reset();
            } catch (PTCPException ex) {
            }
        } catch (PTCPException ex) {
            System.out.println(CommandLine.formatException(ex));
        }

        return context.doStateTransition(new TransmissionFailedState(this));
    }

    private void checkFlags(PTCPFlag psiTP4Flag) throws PTCPException {
        if (psiTP4Flag.equals(PTCPFlag.RST)) {
            throw new PTCPConnectionResetException();
        } else if (!psiTP4Flag.equals(PTCPFlag.NONE)) {
            throw new PTCPProtocolException("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private long saveFile(byte[] data, String fileName) throws PTCPException {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);
            stream.write(data);
            return data.length;
        } catch (FileNotFoundException ex) {
            throw new PTCPException("Unable to save the file!", ex);
        } catch (IOException ex) {
            throw new PTCPException("Unable to write data to the file!", ex);
        }
    }
}
