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

import cz.cvut.fel.psi.udp.application.ProgressLogger;
import cz.cvut.fel.psi.udp.application.ProgressLoggerFactory;
import cz.cvut.fel.psi.udp.core.Connection;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class TransmissionStateMachine {

    protected Context context;
    protected State firstState;
    protected ProgressLogger progressLogger;

    public TransmissionStateMachine(Connection connection) {
        this.context = new Context(connection);
        this.progressLogger = ProgressLoggerFactory.getLogger();
    }

    public void download(String localFileName) {
        if (configureDownload(localFileName)) {
            run();
        }
    }

    public void upload(String firmwareFileName) {
        if (configureUpload(firmwareFileName)) {
            run();
        }
    }

    public void run() {
        StateTransitionStatus transitionStatus = context.doStateTransition(firstState);
        while (transitionStatus == StateTransitionStatus.Continue) {
            State currentState = context.getCurrentState();
            progressLogger.onStateEntered(currentState);
            transitionStatus = currentState.process(context);
            progressLogger.onStateExited(currentState);
        }
    }

    public abstract boolean configureDownload(String localFileName);

    public abstract boolean configureUpload(String firmwareFileName);
}
