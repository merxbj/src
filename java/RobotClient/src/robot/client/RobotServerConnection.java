/*
 * RobotServerConnection
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

package robot.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import robot.common.networking.SocketUtils;
import robot.common.request.Request;
import robot.common.response.Response;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotServerConnection {

    private InetAddress address;
    private int port;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ServerResponseFactory factory;

    public RobotServerConnection(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.factory = new ServerResponseFactory();
    }

    public Response connect() throws IOException {
        this.socket = new Socket(address, port);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        String rawResponse = SocketUtils.readStringFromStream(in);
        return factory.parseResponse(rawResponse);
    }

    public void disconnect() {
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (Exception ex) {}
    }

    public Response processRequest(Request req) throws IOException {
        SocketUtils.sendStringToStream(req.formatForTcp(), out);
        String rawResponse = SocketUtils.readStringFromStream(in);
        return factory.parseResponse(rawResponse);
    }

}