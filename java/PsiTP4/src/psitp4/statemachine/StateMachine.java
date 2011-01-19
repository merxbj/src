/*
 * StateMachine
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

import psitp4.application.ProgressSink;
import psitp4.core.ConnectionResetException;
import psitp4.core.PsiTP4Connection;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class StateMachine {

    private PsiTP4Connection connection;
    private String remoteFileName;
    private String localFileName;
    private ProgressSink sink;

    public StateMachine(PsiTP4Connection connection, ProgressSink sink) {
        this.connection = connection;
        this.sink = sink;
    }

    public void transfer(String remoteFileName, String localFileName) {
        this.remoteFileName = remoteFileName;
        this.localFileName = localFileName;

        try {
            TransmissionState currentState = new WaitingForConnectionState();
            while (currentState != null) {
                sink.onChangedState(currentState);
                currentState = currentState.process(this);
            }
        } catch (ConnectionResetException ex) {
            System.out.println("Connection forcibly hung up by the remote side! Transmission failed!");
        }
    }

    public PsiTP4Connection getConnection() {
        return connection;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public ProgressSink getSink() {
        return sink;
    }

}
