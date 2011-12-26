/*
 * FileTransferer
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

package psitp4.core;

import java.net.InetAddress;
import psitp4.application.ProgressSink;
import psitp4.statemachine.StateMachine;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class FileTransporter {

    public static final short SLIDING_WINDOW_SIZE = 2048;

    private ProgressSink sink;

    public FileTransporter(ProgressSink sink) {
        this.sink = sink;
    }

    public void download(InetAddress hostname, int port, String localFileName) {
        PsiTP4Connection connection =  new PsiTP4Connection(hostname, port, sink, PsiTP4ConnectionType.DOWNLOAD);
        StateMachine machine = new StateMachine(connection, sink);
        machine.download(localFileName);
    }
    
    public void upload(InetAddress hostname, int port, String firmwareFileName) {
        PsiTP4Connection connection =  new PsiTP4Connection(hostname, port, sink, PsiTP4ConnectionType.UPLOAD);
        StateMachine machine = new StateMachine(connection, sink);
        machine.upload(firmwareFileName);
    }
}
