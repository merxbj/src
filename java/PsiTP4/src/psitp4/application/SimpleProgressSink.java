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

package psitp4.application;

import psitp4.core.PsiTP4Connection;
import psitp4.core.PsiTP4Packet;
import psitp4.statemachine.TransmissionState;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SimpleProgressSink implements ProgressSink {

    private long transferredBytes;

    public SimpleProgressSink() {
        this.transferredBytes = 0;
    }

    public void onConnectionClose(PsiTP4Connection con) {
        int conId = (int) con.getId() & 0xffff;
        System.out.println(String.format("Closed connection (%d) with the server ...", conId));
    }

    public void onConnectionOpen(PsiTP4Connection con) {
        System.out.println(String.format("Established connection (%d) with the server ...", con.getId()));
    }

    public void onWindowSlide(long bytes) {
        this.transferredBytes += bytes;
        System.out.println(String.format("### Just transfered %d bytes. Transfered %d in total.", bytes, transferredBytes));
    }

    public void onDataGramReceived(PsiTP4Packet packet) {
        int seq = (int) packet.getSeq() & 0xffff;
        int ack = (int) packet.getAck() & 0xffff;
        System.out.println(String.format("rcv: seq=%d, ack=%d, flg=%s, sze=%d", seq, ack, packet.getFlag(), packet.getData().length));
    }

    public void onDataGramSent(PsiTP4Packet packet) {
        int seq = (int) packet.getSeq() & 0xffff;
        int ack = (int) packet.getAck() & 0xffff;
        System.out.println(String.format("snd: seq=%d, ack=%d, flg=%s, sze=%d", seq, ack, packet.getFlag(), packet.getData().length));
    }

    public void onTransferCompleted(long fileSize) {
        System.out.println(String.format("File transfer completed! Transfered %d bytes.", fileSize));
    }

    public void onChangedState(TransmissionState state) {
        String stateName = state.getClass().getSimpleName();
        System.out.println(String.format("Changed current transmission state to %s", stateName));
    }

    public void onConnectionReset(PsiTP4Connection con) {
        System.out.println(String.format("Reset connection (%d) with the server ...", con.getId()));
    }

}
