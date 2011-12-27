/*
 * RemoteSideDisconnectedState
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
import cz.cvut.fel.psi.udp.core.PsiTP4Connection;
import cz.cvut.fel.psi.udp.core.exception.PsiTP4Exception;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RemoteSideDisconnectedState implements TransmissionState {

    public TransmissionState process(StateMachine machine) {
        try {
            PsiTP4Connection connection = machine.getConnection();
            if (connection != null) {
                connection.close();
                return new TransmissionSuccessfulState();
            }
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }
        return new TransmissionFailedState(this);
    }

}
