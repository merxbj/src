/*
 * FileUploadFinishedState
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

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPFinishedPacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPFlag;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPPacket;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPProtocolException;
import cz.cvut.fel.psi.udp.statemachine.Context;
import cz.cvut.fel.psi.udp.statemachine.StateTransitionStatus;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class FileUploadFinishedState extends PTCPState {

    UnsignedShort finishingSequence;

    public FileUploadFinishedState(UnsignedShort finishingSequence) {
        this.finishingSequence = finishingSequence;
    }

    @Override
    public StateTransitionStatus process(Context context) {

        try {

            boolean remoteSideFinished = false;
            while (!remoteSideFinished) {
                connection.send(new PTCPFinishedPacket(finishingSequence));
                PTCPPacket packet = connection.receive();
                remoteSideFinished = isValidFinishPacket(packet);
            }
            return context.doStateTransition(new TransmissionSuccessfulState());

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

    private boolean isValidFinishPacket(PTCPPacket packet) {
        return ((packet != null)
                && (packet.getSeq().equals(finishingSequence))
                && (packet.getAck().equals(new UnsignedShort(0)))
                && (packet.getFlag().equals(PTCPFlag.FIN)
                && (packet.getData().length == 0)));
    }
}
