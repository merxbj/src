/*
 * CommandState
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
import psitp4.core.GetPacket;
import psitp4.core.PsiTP4Connection;
import psitp4.core.PsiTP4Exception;
import psitp4.core.PsiTP4Packet;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandState implements TransmissionState {

    public TransmissionState process(StateMachine machine) {
        try {
            PsiTP4Connection connection = machine.getConnection();
            if (connection != null) {
                String remoteFileName = machine.getRemoteFileName();
                PsiTP4Packet lastPacket = connection.getHistory().pop();
                PsiTP4Packet getCommandPacket = new GetPacket(remoteFileName);
                getCommandPacket.setSeq(lastPacket.getAck());
                getCommandPacket.setAck((short) (lastPacket.getSeq() + 1));
                connection.send(getCommandPacket);

                short expectedAck = (short) (getCommandPacket.getSeq() + 3 + remoteFileName.length()); // 3 for "GET"
                return new WaitingForAckState(expectedAck, new CommandFinishedState(), new TransmissionFailedState(this));
            }
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }
        return new TransmissionFailedState(this);
    }

}
