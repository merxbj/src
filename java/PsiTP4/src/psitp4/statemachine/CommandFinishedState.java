/*
 * CommandFinishedState
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

package psitp4.statemachine;

import psitp4.application.CommandLine;
import psitp4.core.PsiTP4Connection;
import psitp4.core.PsiTP4Exception;
import psitp4.core.PsiTP4Packet;
import psitp4.core.ResponsePacket;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandFinishedState implements TransmissionState {

    public TransmissionState process(StateMachine machine) {
        try {
            PsiTP4Connection connection = machine.getConnection();
            if (connection != null) {
                PsiTP4Packet lastPacket = connection.getHistory().pop();
                PsiTP4Packet getCommandFinishedPacket = new ResponsePacket(lastPacket.getSeq());
                getCommandFinishedPacket.setSeq(lastPacket.getAck());
                connection.send(getCommandFinishedPacket);

                short expectedAck = (short) (getCommandFinishedPacket.getSeq() + 1);
                return new WaitingForAckState(expectedAck, new FileTransmissionState(), new TransmissionFailedState(this));
            }
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }
        return new TransmissionFailedState(this);
    }

}
