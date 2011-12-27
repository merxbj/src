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

package cz.cvut.fel.psi.udp.statemachine;

import cz.cvut.fel.psi.udp.application.ProgressSink;
import cz.cvut.fel.psi.udp.core.exception.ConnectionResetException;
import cz.cvut.fel.psi.udp.core.PsiTP4Connection;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class StateMachine {

    private PsiTP4Connection connection;
    private String localFileName;
    private ProgressSink sink;
    private TransmissionState fileTransmissionState;

    public StateMachine(PsiTP4Connection connection, ProgressSink sink) {
        this.connection = connection;
        this.sink = sink;
        this.fileTransmissionState = null;
        this.localFileName = null;
    }

    public void download(String localFileName) {
        this.localFileName = localFileName;
        this.fileTransmissionState = new FileDownloadState();

        run();
    }
    
    public void upload(String firmwareFileName) {
        this.localFileName = firmwareFileName;
        this.fileTransmissionState = new FileUploadState();

        run();
    }
    
    public void run() {
        try {
            TransmissionState lastState = null;
            TransmissionState currentState = new WaitingForConnectionState();
            while (currentState != null) {
                sink.onChangedState(lastState, currentState);
                lastState = currentState;
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

    public ProgressSink getSink() {
        return sink;
    }

    public TransmissionState getFileTransmissionState() {
        return fileTransmissionState;
    }

}
