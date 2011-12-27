/*
 * SinkFactory
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
 * @author eTeR
 * @version %I% %G%
 */
public final class ProgressSinkFactory {

    private ProgressSinkFactory() {
    }

    public static ProgressSink newSinkFactory(final CommandLine.LogLevel logLevel) {
        switch (logLevel) {
            case Verboose:
                return new VerbooseProgressSink();
            case Normal:
                return new NormalProgressSink();
            case Simple:
                return new SimpleProgressSink();
            default:
                return new ProgressSink() {

                    public void onWindowSlide(final long bytes) {
                        return;
                    }

                    public void onDataGramReceived(final PsiTP4Packet packet) {
                        return;
                    }

                    public void onDataGramSent(final PsiTP4Packet packet) {
                        return;                    }

                    public void onConnectionOpen(final PsiTP4Connection con) {
                        return;
                    }

                    public void onConnectionClose(final PsiTP4Connection con) {
                        return;
                    }

                    public void onTransferCompleted(final long fileSize) {
                        return;
                    }

                    public void onChangedState(final TransmissionState from, final TransmissionState to) {
                        return;
                    }

                    public void onConnectionReset(final PsiTP4Connection con) {
                        return;
                    }
                };
        }
    }
}
