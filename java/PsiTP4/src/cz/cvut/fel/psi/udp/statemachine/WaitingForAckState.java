/*
 * WaitingForAckState
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

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.exception.ConnectionResetException;
import cz.cvut.fel.psi.udp.core.PsiTP4Connection;
import cz.cvut.fel.psi.udp.core.exception.PsiTP4Exception;
import cz.cvut.fel.psi.udp.core.PsiTP4Flag;
import cz.cvut.fel.psi.udp.core.PsiTP4Packet;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WaitingForAckState implements TransmissionState {

    private TransmissionState okState;
    private TransmissionState failState;
    private short expectedAck;

    public WaitingForAckState(short expectedAck, TransmissionState okState, TransmissionState failState) {
        this.okState = okState;
        this.failState = failState;
        this.expectedAck = expectedAck;
    }

    public TransmissionState process(StateMachine machine) throws ConnectionResetException {
        try {
            PsiTP4Connection connection = machine.getConnection();
            if (connection != null) {
                PsiTP4Packet ackPacket = connection.receive();
                if (ackPacket.getFlag().equals(PsiTP4Flag.RST)) {
                    throw new ConnectionResetException();
                } 
                if (ackPacket.getAck() == expectedAck)
                    return okState;
                }
        } catch (ConnectionResetException ex) {
            throw ex;
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }
        return failState;
    }

}
