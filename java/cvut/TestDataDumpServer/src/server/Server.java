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

package server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Server {

    public static void main(String[] args) {

        boolean quit = false;

        try {
            ServerSocket ss = new ServerSocket(5451);

            while (!quit) {
                final Socket sock = ss.accept();
                Thread t = new Thread(new ClientProcess(sock));
                t.setDaemon(true);
                t.start();
            }

            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
