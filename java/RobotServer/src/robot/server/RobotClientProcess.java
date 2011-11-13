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
import robot.common.networking.SocketUtils;
import robot.common.response.ResponseIdentification;
import robot.server.logging.Logger;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotClientProcess implements Runnable {

    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;
    private ClientRequestFactory requestFactory;
    private ClientRequestProcessor requestProcessor;
    private Robot robot;
    private Logger log;

    public void run() {

        try {

            this.in = clientSocket.getInputStream();
            this.out = clientSocket.getOutputStream();
            log.logMessage("Client connected! Going to send him the robot identification address %s!", robot.getName());
            sendResponseToSocket(new ResponseIdentification(robot.getName()));

            boolean quit = false;
            while (!quit) {

                try {
                    String rawRequest = readRequestFromSocket();
                    log.logMessage("Read request message %s from the client %s!", rawRequest, clientSocket.getInetAddress());

                    Request request = requestFactory.parseRequest(rawRequest);
                    log.logRequest(request);

                    Response response = request.process(requestProcessor);
                    log.logResponse(response);

                    sendResponseToSocket(response);
                    quit = response.isEndGame();
                } catch (IOException ex) {
                    clientSocket.close();
                    log.logMessage("Connection lost.");
                    quit = true;
                }
            }

        } catch (Exception ex) {
            log.logException(ex);
        } finally {
            try {
                goodBye();
                if (clientSocket != null) {
                    in.close();
                    out.close();
                    clientSocket.close();
                }
            } catch (Exception ex) {
                log.logException(ex);
            }
        }
    }

    public RobotClientProcess(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.robot = new Robot(RobotNameProvider.provideName());
        this.requestFactory = new ClientRequestFactory(robot.getName());
        this.requestProcessor = new ClientRequestProcessor(robot);
        this.log = Logger.getLogger(this.robot);
    }

    private String readRequestFromSocket() throws IOException {
        return SocketUtils.readStringFromStream(in);
    }

    private void sendResponseToSocket(Response response) throws IOException {
        SocketUtils.sendStringToStream(response.formatForTcp(), out);
    }

    private void goodBye() {
        RobotNameProvider.freeName(robot.getName());
    }

}
