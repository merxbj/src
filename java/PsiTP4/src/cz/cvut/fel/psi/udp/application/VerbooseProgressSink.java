/*
 * ProgressSink
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
package cz.cvut.fel.psi.udp.application;

import cz.cvut.fel.psi.udp.core.PsiTP4Connection;
import cz.cvut.fel.psi.udp.core.PsiTP4Packet;
import cz.cvut.fel.psi.udp.statemachine.TransmissionState;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class VerbooseProgressSink implements ProgressSink {

    private long transferredBytes;

    public VerbooseProgressSink() {
        this.transferredBytes = 0;
    }

    public void onConnectionClose(final PsiTP4Connection con) {
        int conId = (int) con.getId() & 0xffff;
        System.out.println(String.format("Connection (%d) state change: CLOSED", conId));
    }

    public void onConnectionOpen(final PsiTP4Connection con) {
        int conId = (int) con.getId() & 0xffff;
        System.out.println(String.format("Connection (%d) state change: OPENED", conId));
    }

    public void onWindowSlide(long bytes) {
        this.transferredBytes += bytes;
        System.out.println(String.format("Window slide: Just transfered %d bytes. Transfered %d in total.", bytes, transferredBytes));
    }

    public void onDataGramReceived(final PsiTP4Packet packet) {
        int seq = (int) packet.getSeq() & 0xffff;
        int ack = (int) packet.getAck() & 0xffff;
        int con = (int) packet.getCon() & 0xffff;
        System.out.println(String.format("\trcv: con=%d, seq=%d, ack=%d, flg=%s, sze=%d", con, seq, ack, packet.getFlag(), packet.getData().length));
    }

    public void onDataGramSent(final PsiTP4Packet packet) {
        int seq = (int) packet.getSeq() & 0xffff;
        int ack = (int) packet.getAck() & 0xffff;
        int con = (int) packet.getCon() & 0xffff;
        System.out.println(String.format("\tsnd: con=%d, seq=%d, ack=%d, flg=%s, sze=%d", con, seq, ack, packet.getFlag(), packet.getData().length));
    }

    public void onTransferCompleted(long fileSize) {
        System.out.println(String.format("File transfer status: COMPLETED. Transfered %d bytes.", fileSize));
    }

    public void onChangedState(final TransmissionState from, final TransmissionState to) {
        String fromName = from != null ? from.getClass().getSimpleName() : null;
        String toName = to != null ? to.getClass().getSimpleName() : null;
        System.out.println(String.format("State machine notification: %s -> %s", fromName, toName));
    }

    public void onConnectionReset(final PsiTP4Connection con) {
        int conId = (int) con.getId() & 0xffff;
        System.out.println(String.format("Connection (%d) state change: RESET", conId));
    }
}
