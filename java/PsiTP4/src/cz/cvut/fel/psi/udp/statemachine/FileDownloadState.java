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

package cz.cvut.fel.psi.udp.statemachine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.exception.ConnectionResetException;
import cz.cvut.fel.psi.udp.core.PsiTP4Connection;
import cz.cvut.fel.psi.udp.core.exception.PsiTP4Exception;
import cz.cvut.fel.psi.udp.core.PsiTP4Flag;
import cz.cvut.fel.psi.udp.core.PsiTP4Packet;
import cz.cvut.fel.psi.udp.core.ResponsePacket;
import cz.cvut.fel.psi.udp.core.SlidingWindow;
import cz.cvut.fel.psi.udp.core.exception.ProtocolException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class FileDownloadState implements TransmissionState {

    public static final short SLIDING_WINDOW_SIZE = 2048;

    public TransmissionState process(StateMachine machine) throws ConnectionResetException {
        PsiTP4Connection connection = machine.getConnection();
        try {
            if (connection != null) {
                SlidingWindow window = new SlidingWindow(connection.getSequence(), SLIDING_WINDOW_SIZE, machine.getSink());

                PsiTP4Packet received = connection.receive();
                while (received.getFlag() != PsiTP4Flag.FIN) {

                    checkFlags(received.getFlag());

                    short ack = window.push(received.getData(), received.getSeq());
                    PsiTP4Packet response = new ResponsePacket(ack);
                    response.setSeq(received.getAck());
                    connection.send(response);

                    received = connection.receive();
                }

                long size = saveFile(window.pull(), machine.getLocalFileName());
                machine.getSink().onTransferCompleted(size);
                return new RemoteSideDisconnectedState();
            }

        } catch (ConnectionResetException ex) {
            throw ex;
        } catch (ProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            try {
                connection.reset();
            } catch (PsiTP4Exception ex) {}
        } 
        catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }

        return new TransmissionFailedState(this);
    }

    private void checkFlags(PsiTP4Flag psiTP4Flag) throws PsiTP4Exception {
        if (psiTP4Flag.equals(PsiTP4Flag.RST)) {
            throw new ConnectionResetException("Error occured during the transmission! Exiting...");
        } else if (!psiTP4Flag.equals(PsiTP4Flag.NONE)) {
            throw new PsiTP4Exception("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private long saveFile(byte[] data, String fileName) throws PsiTP4Exception {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);
            stream.write(data);
            return data.length;
        } catch (FileNotFoundException ex) {
            throw new PsiTP4Exception("Unable to save the file!", ex);
        } catch (IOException ex) {
            throw new PsiTP4Exception("Unable to write data to the file!", ex);
        }
    }

}
