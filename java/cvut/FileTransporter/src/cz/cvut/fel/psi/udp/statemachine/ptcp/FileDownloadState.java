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
import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPConnectionResetException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPFlag;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPPacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPResponsePacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPInboundSlidingWindow;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPProtocolException;
import cz.cvut.fel.psi.udp.statemachine.Context;
import cz.cvut.fel.psi.udp.statemachine.StateTransitionStatus;
import java.io.OutputStream;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class FileDownloadState extends PTCPState {

    private String localFileName;
    private PTCPInboundSlidingWindow window;

    public FileDownloadState(String localFileName) {
        this.localFileName = localFileName;
        this.window = new PTCPInboundSlidingWindow();
    }

    public StateTransitionStatus process(Context context) {
        try {

            window.init(new UnsignedShort(0), openFileStream());

            PTCPPacket received = connection.receive();
            while ((received == null) || (received.getFlag() != PTCPFlag.FIN)) {

                if (received != null) {
                    checkFlags(received.getFlag());

                    if (window.accept(received.getData(), received.getSeq())) {
                        window.slideWindow();
                    }

                    PTCPPacket response = new PTCPResponsePacket(window.getBegin());
                    response.setSeq(received.getAck());
                    connection.send(response);
                }

                received = connection.receive();
            }

            return context.doStateTransition(new RemoteSideDisconnectingState(received, window.getBegin()));

        } catch (PTCPProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            return context.doStateTransition(new TransmissionAbortedState());
        } catch (PTCPException ex) {
            System.out.println(CommandLine.formatException(ex));
        } finally {
            window.finish();
        }

        return context.doStateTransition(new TransmissionFailedState());
    }

    private void checkFlags(PTCPFlag ptcpFlag) throws PTCPException {
        if (ptcpFlag.equals(PTCPFlag.RST)) {
            throw new PTCPConnectionResetException();
        } else if (!ptcpFlag.equals(PTCPFlag.NONE)) {
            throw new PTCPProtocolException("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private OutputStream openFileStream() throws PTCPException {
        try {
            FileOutputStream stream = new FileOutputStream(localFileName);
            return stream;
        } catch (FileNotFoundException ex) {
            throw new PTCPException("Unable to open the download file for writing!", ex);
        }
    }
}
