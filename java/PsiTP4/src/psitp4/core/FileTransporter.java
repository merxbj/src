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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import psitp4.application.CommandLine;
import psitp4.application.ProgressSink;

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

    public long transfer(InetAddress hostname, int port, String remoteFileName, String localFileName) {
        long size = 0;
        PsiTP4Connection connection =  new PsiTP4Connection(hostname, port, sink);
        SlidingWindow window = new SlidingWindow(SLIDING_WINDOW_SIZE, sink);

        try {
            connection.open();
            connection.send(new GetPacket(remoteFileName));

            PsiTP4Packet received = connection.receive();
            while (received.getFlag() != PsiTP4Flag.FIN) {

                checkFlags(received.getFlag());

                short ack = window.push(received.getData(), received.getSeq());
                connection.send(new ResponsePacket(ack));

                received = connection.receive();
            }

            size = saveFile(window.pull(), localFileName);
            connection.close();

        } catch (ConnectionResetException ex) {
            System.out.println(CommandLine.formatException(ex));
            System.out.println("Connection has been already closed by the other side.");
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
            try {
                connection.close();
            } catch (PsiTP4Exception e) {}
        }

        return size;
    }

    private void checkFlags(PsiTP4Flag psiTP4Flag) throws PsiTP4Exception {
        if (psiTP4Flag.equals(PsiTP4Flag.RST)) {
            throw new ConnectionResetException("Error occured during the transmission! Exiting...");
        } else if (!psiTP4Flag.equals(PsiTP4Flag.NONE)) {
            throw new PsiTP4Exception("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private long saveFile(byte[] data, String fileName) throws PsiTP4Exception {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);
            stream.write(data);
            return data.length;
        } catch (FileNotFoundException ex) {
            throw new PsiTP4Exception("Unable to save the file!", ex);
        } catch (IOException ex) {
            throw new PsiTP4Exception("Unable to write data to the file!", ex);
        }
    }

}
