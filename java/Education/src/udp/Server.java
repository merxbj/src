/*
 * Server
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

package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Server {
    public static void main(String[] args) {
        System.out.println("Listeining on port 1234 ...");
        System.out.println("<Ctrl-c> to quit ...");
        try {
            DatagramSocket socket = new DatagramSocket(1234);
            byte[] buf = new byte[1024];
            DatagramPacket received = new DatagramPacket(buf, buf.length);
            
            while (true) {

                socket.receive(received);

                String data = new String(received.getData());
                System.out.println(data);
                data = data.toUpperCase();
                System.out.println(data);

                buf = data.getBytes();
                DatagramPacket sent = new DatagramPacket(buf, buf.length, received.getAddress(), received.getPort());
                socket.send(sent);

            }

        } catch (Exception ex) {
            System.out.println(formatException(ex));
        }
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    public static String formatException(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString());
        sb.append("\n");
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\t at ");
            sb.append(ste.toString());
            sb.append("\n");
        }

        Throwable innerException = ex.getCause();
        while (innerException != null) {
            sb.append("\t caused by ");
            sb.append(formatException(innerException));
            sb.append("\n");
            innerException = innerException.getCause();
        }

        return sb.toString();
    }
}
