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

import cz.cvut.fel.psi.udp.core.Connection;
import cz.cvut.fel.psi.udp.core.Packet;
import cz.cvut.fel.psi.udp.statemachine.State;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class VerbooseProgressLogger implements ProgressLogger {

    private long transferredBytes;

    public VerbooseProgressLogger() {
        this.transferredBytes = 0;
    }

    public void onConnectionClose(final Connection con) {
        System.out.println(String.format("Connection (%s) state change: CLOSED", con));
    }

    public void onConnectionOpen(final Connection con) {
        System.out.println(String.format("Connection (%s) state change: OPENED", con));
    }

    public void onWindowSlide(long bytes) {
        this.transferredBytes += bytes;
        System.out.println(String.format("Sliding window: Just slided %d bytes. Slided %d in total.", bytes, transferredBytes));
    }

    public void onDataGramReceived(final Packet packet) {
        System.out.println(String.format("\trcv: %s", packet));
    }

    public void onDataGramSent(final Packet packet) {
        System.out.println(String.format("\tsnd: %s", packet));
    }

    public void onStateEntered(final State entered) {
        System.out.println(String.format("State machine notification: Entered %s.", entered));
    }
    
    public void onStateExited(final State exited) {
        System.out.println(String.format("State machine notification: Exited %s.", exited));
    }
    
    public void onConnectionReset(final Connection con) {
        System.out.println(String.format("Connection (%s) state change: RESET", con));
    }
}
