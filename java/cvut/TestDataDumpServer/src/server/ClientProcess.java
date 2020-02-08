/*
 * ClientProcess
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

package server;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ClientProcess implements Runnable{

    private final Socket clientSocket;

    public void run() {
        try {

            System.out.println("Established connection!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder builder = new StringBuilder();
            
            while (reader.ready()) {
                builder.append(reader.readLine());
            }

            PrintStream out = new PrintStream(new File(String.format("TestData%d.txt", System.currentTimeMillis() / 1000)));
            out.print(builder.toString());

            clientSocket.close();
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public ClientProcess(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
