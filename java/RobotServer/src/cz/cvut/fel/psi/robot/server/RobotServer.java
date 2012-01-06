/*
 * RobotServer
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

package cz.cvut.fel.psi.robot.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotServer {

    private int listeiningPort;

    public RobotServer(CommandLine params) {
        this.listeiningPort = params.getPortNumber();
    }

    public void run() {
        try {
            boolean quit = false;
            ServerSocket ss = new ServerSocket(listeiningPort);

            while (!quit) {
                final Socket sock = ss.accept();
                Thread t = new Thread(new RobotClientProcess(sock));
                t.start();
            }

            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
