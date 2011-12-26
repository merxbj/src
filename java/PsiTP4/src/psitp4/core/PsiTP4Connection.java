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

import psitp4.core.exception.PsiTP4Exception;
import psitp4.core.exception.DeserializationException;
import psitp4.core.exception.SerializationException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import psitp4.application.ProgressSink;
import psitp4.core.exception.ProtocolException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PsiTP4Connection {

    private static final int OPEN_CONNECTION_TIMEOUT = 100;
    private static final int OPEN_CONNECTION_ATTEMPTS = 20;
    
    private InetAddress hostname;
    private int port;
    private DatagramSocket socket;
    private int id;
    private ProgressSink sink;
    private short sequence;
    private PsiTP4ConnectionType type;

    public PsiTP4Connection(InetAddress hostname, int port, ProgressSink sink, PsiTP4ConnectionType type) {
        this.hostname = hostname;
        this.port = port;
        this.id = 0;
        this.sink = sink;
        this.sequence = 0;
        this.type = type;
    }

    public void open() throws PsiTP4Exception {
        
        try {

            this.socket = new DatagramSocket();
            OpenConnectionPacket openRequest = new OpenConnectionPacket(type);
            PsiTP4Packet openResponse = null;

            int connectionAttempts = OPEN_CONNECTION_ATTEMPTS;
            while ((openResponse == null) && (connectionAttempts-- > 0)) {
                this.send(openRequest);
                openResponse = this.receive(OPEN_CONNECTION_TIMEOUT);
            }

            if (!openResponse.getFlag().equals(PsiTP4Flag.SYN)) {
                throw new ProtocolException("Server responded with invalid flags! Expected SYN, got " + openResponse.getFlag().toString());
            }

            if (openResponse.getAck() != 0 || openResponse.getSeq() != 0) {
                throw new ProtocolException(String.format("Server responded with invalid ACK value! Expected %d, got %d", openRequest.getSeq() + 1, openResponse.getAck()));
            }
            
            if (!PsiTP4ConnectionType.fromDataArray(openResponse.getData()).equals(type)) {
                throw new ProtocolException("Server responded with SYN packet with command that does not match the requested command.");
            }
            
            if (openResponse.getCon() == 0) {
                throw new ProtocolException("Server responded with SYN packet with zero connection id.");
            }

            this.id = openResponse.getCon();

            sink.onConnectionOpen(this);

        } catch (SocketException ex) {
            throw new PsiTP4Exception("Unable to bind the socket!", ex);
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
    
    public void reset() throws PsiTP4Exception {
        
        ResetPacket resetPacket = new ResetPacket(this.id);
        this.send(resetPacket);
        
        sink.onConnectionReset(this);
    }

    public void send(PsiTP4Packet packet) throws PsiTP4Exception {
        if (isConnected()) {
            try {
                packet.setCon(this.id);
                byte[] snd = packet.serialize();
                DatagramPacket toSend = new DatagramPacket(snd, snd.length, hostname, port);
                socket.send(toSend);
                sink.onDataGramSent(packet);
                sequence = packet.getSeq();
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
        return receive(0);
    }
    
    public PsiTP4Packet receive(int timeout) throws PsiTP4Exception {
        if (isConnected()) {
            try {
                byte[] rcv = new byte[PsiTP4Packet.MAX_SIZE];
                DatagramPacket response = new DatagramPacket(rcv, rcv.length);
                socket.setSoTimeout(timeout);
                socket.receive(response);

                PsiTP4Packet received = new PsiTP4Packet();
                received.deserialize(response.getData(), response.getLength());

                sink.onDataGramReceived(received);
                
                if ((id != 0) && (received.getCon() != id)) {
                    throw new ProtocolException("Received packet addressed to different connection id.");
                }
                
                sequence = received.getSeq();
                return received;
            } catch (SocketTimeoutException ste) {
                return null;
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

    public short getSequence() {
        return sequence;
    }
}
