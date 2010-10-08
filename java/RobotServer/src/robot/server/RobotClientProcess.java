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
import robot.common.response.ResponseIdentification;
import robot.server.exception.InvalidAddressException;
import robot.server.logging.Logger;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotClientProcess implements Runnable, Resumable {

    private Socket clientSocket;
    private ClientRequestFactory requestFactory;
    private ClientRequestProcessor requestProcessor;
    RobotRequestRouter router;
    private Robot robot;
    private Logger log;
    private boolean waiting;

    public void run() {

        router.registerProcess(this, robot.getName());

        try {

            log.logMessage("Client connected! Going to send him the robot identification address %s!", robot.getName());
            sendResponseToSocket(new ResponseIdentification(robot.getName()));

            boolean quit = false;
            while (!quit) {

                try {
                    String rawRequest = readRequestFromSocket();
                    log.logMessage("Read request message %s from the client!", rawRequest);

                    Request request = requestFactory.parseRequest(rawRequest);
                    log.logRequest(request);

                    Response response = request.process(requestProcessor);
                    log.logResponse(response);

                    sendResponseToSocket(response);
                    quit = response.isEndGame();
                } catch (IOException ex) {

                    log.logMessage("Connection lost. Let's wait a minute for reconnect if it makes sense ...");
                    if (!quit && waitForReconnect(60000)) {
                        log.logMessage("Connection reestablished! Let's keep going!");
                    } else if (quit) {
                        log.logMessage("Will not wait because the end game has been already reached!");
                    } else {
                        log.logMessage("Giving up waiting. Goodbye()!");
                        quit = true;
                    }
                } catch (InvalidAddressException ex) {
                    log.logMessage("Received unexpected address %s from the client. Expected was %s.", ex.getRequestedAddress(), ex.getExpectedAddress());
                    if (router.routeAddress(ex.getRequestedAddress(), clientSocket)) {
                        goodBye();
                        return; // someone took over the socket, leave without closing it
                    }
                }

            }

        } catch (Exception ex) {
            log.logException(ex);
        } finally {
            try {
                goodBye();
                clientSocket.close();
            } catch (Exception ex) {
                log.logException(ex);
            }
        }
    }

    public RobotClientProcess(Socket clientSocket, RobotRequestRouter router) {
        this.clientSocket = clientSocket;
        this.robot = new Robot(RobotNameProvider.provideName());
        this.requestFactory = new ClientRequestFactory(robot.getName());
        this.requestProcessor = new ClientRequestProcessor(robot);
        this.log = Logger.getLogger(this.robot.getName());
        this.router = router;
        this.waiting = false;
    }

    private String readRequestFromSocket() throws IOException {
        InputStreamReader in = new InputStreamReader(clientSocket.getInputStream(), "US-ASCII");
        StringBuilder builder = new StringBuilder();

        try {
            while (true) {
                char ch = (char) in.read();
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

    private void sendResponseToSocket(Response response) throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        char[] chars = response.formatForTcp().toCharArray();
        
        for (Character ch : chars) {
            out.writeChar((int) ch);
        }
    }

    private boolean waitForReconnect(int timeout) throws Exception {
        synchronized (this) {
            if (clientSocket.isClosed()) {
                waiting = true;
                this.wait(timeout); // we have lost the connection - lets wait some time
                waiting = false;
                return clientSocket.isConnected();
            }
            return false;
        }
    }

    private void goodBye() {
        router.unregisterProcess(robot.getName());
        RobotNameProvider.freeName(robot.getName());
    }

    public boolean resume(Socket newSocket) {
        if (waiting) {
            synchronized (this) {
                this.notify();
                return true;
            }
        }
        return false;
    }


}
