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
 * @authoer %I% %G%
 */
public interface ProgressSink {

    void onWindowSlide(long bytes);

    void onDataGramReceived(final PsiTP4Packet packet);

    void onDataGramSent(final PsiTP4Packet packet);

    void onConnectionOpen(final PsiTP4Connection con);

    void onConnectionClose(final PsiTP4Connection con);

    void onTransferCompleted(long fileSize);

    void onChangedState(final TransmissionState from, final TransmissionState to);

    void onConnectionReset(final PsiTP4Connection con);
}
