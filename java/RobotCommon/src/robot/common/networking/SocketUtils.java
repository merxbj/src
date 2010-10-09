/*
 * SocketUtils
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

package robot.common.networking;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SocketUtils {

    public static String readStringFromSocket(Socket socket) throws IOException {

        InputStreamReader in = new InputStreamReader(socket.getInputStream(), "US-ASCII");
        StringBuilder builder = new StringBuilder();

        try {
            while (true) {
                int i = in.read();
                if (i == -1) {
                    socket.close();
                    throw new IOException("Unexpected end of stram reached!");
                }

                char ch = (char) i;
                if (ch == '\r') {
                    ch = (char) in.read();
                    if (ch == '\n') {
                        break;
                    } else {
                        return "";
                    }
                }
                builder.append(ch);
            }
        } catch (EOFException ex) {
            return "";
        }

        return builder.toString();
    }

    public static void sendStringToSocket(String data, Socket socket) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        char[] chars = data.toCharArray();

        for (Character ch : chars) {
            out.writeChar((int) ch);
        }
    }

}
