/*
 * PsiTP4Connection
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import psitp4.application.ProgressSink;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PsiTP4Connection {

    private InetAddress hostname;
    private int port;
    private DatagramSocket socket;
    private int id;
    private ProgressSink sink;
    private short seq;

    public PsiTP4Connection(InetAddress hostname, int port, ProgressSink sink) {
        this.hostname = hostname;
        this.port = port;
        this.id = 0;
        this.seq = 0;
        this.sink = sink;
    }

    public void open() throws PsiTP4Exception {
        
        try {

            this.socket = new DatagramSocket();

            OpenConnectionPacket openRequest = new OpenConnectionPacket();

            byte[] snd = openRequest.serialize();
            DatagramPacket request = new DatagramPacket(snd, snd.length, hostname, port);
            socket.send(request);
            sink.onDataGramSent(openRequest);

            byte[] rcv = new byte[PsiTP4Packet.MAX_SIZE];
            DatagramPacket response = new DatagramPacket(rcv, rcv.length);
            socket.receive(response);

            PsiTP4Packet openResponse = new PsiTP4Packet();
            openResponse.deserialize(response.getData());
            sink.onDataGramReceived(openResponse);

            if (!openResponse.getFlag().equals(PsiTP4Flag.SYN)) {
                throw new PsiTP4Exception("Server responded with invalid flags! Expected SYN, got " + openResponse.getFlag().toString());
            }

            if (openResponse.getAck() != openRequest.getSeq() + 1) {
                throw new PsiTP4Exception(String.format("Server responded with invalid ACK value! Expected %d, got %d", openRequest.getSeq() + 1, openResponse.getAck()));
            }

            this.id = openResponse.getCon();
            this.seq = openResponse.getAck();

            sink.onConnectionOpen(this);

        } catch (SocketException ex) {
            throw new PsiTP4Exception("Unable to bind the socket!", ex);
        } catch (SerializationException ex) {
            throw new PsiTP4Exception("Invalid packet format - cannot serialize.", ex);
        } catch (DeserializationException ex) {
            throw new PsiTP4Exception("Invalid binary data - cannot deserialize.", ex);
        } catch (IOException ex) {
            throw new PsiTP4Exception("IOException occured - rcv/snd failed on socket.", ex);
        } catch (Exception ex) {
            throw new PsiTP4Exception("Unexpected exception occured!", ex);
        }

    }

    public void close() throws PsiTP4Exception {

        FinishedPacket closeRequest = new FinishedPacket();
        this.send(closeRequest);

        sink.onConnectionClose(this);
    }

    public void send(PsiTP4Packet packet) throws PsiTP4Exception {
        if (isConnected()) {
            try {
                packet.setCon(this.id);
                packet.setSeq(this.seq);
                byte[] snd = packet.serialize();
                DatagramPacket toSend = new DatagramPacket(snd, snd.length, hostname, port);
                socket.send(toSend);
                sink.onDataGramSent(packet);
            } catch (IOException ex) {
                throw new PsiTP4Exception("IOException occured - send failed on socket.", ex);
            } catch (SerializationException ex) {
                throw new PsiTP4Exception("Invalid packet format - cannot serialize!", ex);
            }
        } else {
            throw new PsiTP4Exception("Tried to send() when !isConnected()!");
        }
    }

    public PsiTP4Packet receive() throws PsiTP4Exception {
        if (isConnected()) {
            try {
                byte[] rcv = new byte[PsiTP4Packet.MAX_SIZE];
                DatagramPacket response = new DatagramPacket(rcv, rcv.length);
                socket.receive(response);

                PsiTP4Packet received = new PsiTP4Packet();
                received.deserialize(response.getData());

                sink.onDataGramReceived(received);
                return received;
            } catch (IOException ex) {
                throw new PsiTP4Exception("IOException occured - send failed on socket.", ex);
            } catch (DeserializationException ex) {
                throw new PsiTP4Exception("Invalid binary data - cannot deserialize.", ex);
            }
        } else {
            throw new PsiTP4Exception("Tried to receive() when !isConnected()!");
        }
    }

    private boolean isConnected() {
        return (this.id != 0);
    }

    public int getId() {
        return id;
    }

}
