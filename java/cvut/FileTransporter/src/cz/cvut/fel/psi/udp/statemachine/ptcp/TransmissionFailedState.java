/*
 * TransmissionFailedState
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
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import cz.cvut.fel.psi.udp.statemachine.Context;
import cz.cvut.fel.psi.udp.statemachine.StateTransitionStatus;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class TransmissionFailedState extends PTCPState {

    public StateTransitionStatus process(Context context) {
        try {
            connection.reset();
        } catch (PTCPException ex) {
            System.out.println(CommandLine.formatException(ex));
        } finally {
            try {
                connection.close();
            } catch (PTCPException pex) {
                System.out.println(CommandLine.formatException(pex));
            }
        }
        return StateTransitionStatus.Aborted;
    }
}
