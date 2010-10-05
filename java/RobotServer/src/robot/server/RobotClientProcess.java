/*
 * RobotClientProcess
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

package robot.server;

import robot.common.request.Request;
import robot.common.response.Response;
import java.io.*;
import java.net.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotClientProcess implements Runnable{

    private final Socket clientSocket;
    private final ClientRequestFactory requestFactory;
    private final ClientRequestProcessor requestProcessor;
    private Robot robot;

    public void run() {

        try {

            boolean quit = false;
            while (!quit) {
                String rawRequest = readRequestFromSocket();
                Request request = requestFactory.parseRequest(rawRequest);
                Response response = request.process(requestProcessor);
                sendResponseToSocket(response);
                quit = response.isEndGame();
            }

        } catch (Exception ex) {
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ex) {}
        }
    }

    public RobotClientProcess(final Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.robot = new Robot(RobotNameProvider.provideName());
        this.requestFactory = new ClientRequestFactory(robot.getName());
        this.requestProcessor = new ClientRequestProcessor(robot);
    }

    private String readRequestFromSocket() {
        return "";
    }

    private void sendResponseToSocket(Response response) {
        ;
    }
}
