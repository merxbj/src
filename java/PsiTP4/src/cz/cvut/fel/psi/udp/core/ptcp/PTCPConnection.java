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
package cz.cvut.fel.psi.udp.core.ptcp;

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import cz.cvut.fel.psi.udp.application.ProgressLogger;
import cz.cvut.fel.psi.udp.application.ProgressLoggerFactory;
import cz.cvut.fel.psi.udp.core.Connection;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.exception.DeserializationException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPProtocolException;
import cz.cvut.fel.psi.udp.core.exception.SerializationException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PTCPConnection implements Connection<PTCPPacket> {

    private static final int OPEN_CONNECTION_TIMEOUT = 100;
    private static final int OPEN_CONNECTION_ATTEMPTS = 20;
    private InetAddress hostname;
    private int port;
    private DatagramSocket socket;
    private int id;
    private UnsignedShort sequence;
    private PTCPConnectionType type;
    private boolean connecting;
    private ProgressLogger progressLogger;

    public PTCPConnection(InetAddress hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.id = 0;
        this.sequence = new UnsignedShort(0);
        this.type = PTCPConnectionType.UNDETERMINED;
        this.progressLogger = ProgressLoggerFactory.getLogger();
    }

    public void open() throws PTCPException {

        if (isConnected()) {
            return; // TODO: Add logging here
        }

        setConnecting(true);

        try {

            this.socket = new DatagramSocket();
            PTCPOpenConnectionPacket openRequest = new PTCPOpenConnectionPacket(type);
            PTCPPacket openResponse = null;

            int connectionAttempts = OPEN_CONNECTION_ATTEMPTS;
            boolean connectionEstablished = false;
            while (!connectionEstablished && (connectionAttempts-- > 0)) {
                this.send(openRequest);
                openResponse = this.receive(OPEN_CONNECTION_TIMEOUT);
                if (gotValidOpenResponse(openRequest, openResponse)) {
                    connectionEstablished = true;
                }
            }

            if (!connectionEstablished) {
                throw new PTCPException("Failed to establish a new connection!");
            }
            
            this.id = openResponse.getCon();
            progressLogger.onConnectionOpen(this);

        } catch (PTCPProtocolException pex) {
            throw pex;
        } catch (SocketException ex) {
            throw new PTCPException("Unable to bind the socket!", ex);
        } catch (IOException ex) {
            throw new PTCPException("IOException occured - rcv/snd failed on socket.", ex);
        } catch (Exception ex) {
            throw new PTCPException("Unexpected exception occured!", ex);
        } finally {
            setConnecting(false);
        }

    }

    public void close() throws PTCPException {

        PTCPFinishedPacket closeRequest = new PTCPFinishedPacket(sequence);
        this.send(closeRequest);

        progressLogger.onConnectionClose(this);
    }

    public void reset() throws PTCPException {

        PTCPResetPacket resetPacket = new PTCPResetPacket(this.id);
        this.send(resetPacket);

        progressLogger.onConnectionReset(this);
    }

    public void send(PTCPPacket packet) throws PTCPException {
        if (isConnected()) {
            try {
                packet.setCon(this.id);
                byte[] snd = packet.serialize();
                DatagramPacket toSend = new DatagramPacket(snd, snd.length, hostname, port);
                socket.send(toSend);
                progressLogger.onDataGramSent(packet);
            } catch (IOException ex) {
                throw new PTCPException("IOException occured - send failed on socket.", ex);
            } catch (SerializationException ex) {
                throw new PTCPException("Invalid packet format - cannot serialize!", ex);
            }
        } else {
            throw new PTCPException("Tried to send() when !isConnected()!");
        }
    }

    public PTCPPacket receive() throws PTCPException {
        return receive(0);
    }

    public PTCPPacket receive(int timeout) throws PTCPException {
        if (isConnected()) {
            try {
                byte[] rcv = new byte[PTCPPacket.MAX_SIZE];
                DatagramPacket response = new DatagramPacket(rcv, rcv.length);
                socket.setSoTimeout(timeout);
                socket.receive(response);

                PTCPPacket received = new PTCPPacket();
                received.deserialize(response.getData(), response.getLength());

                progressLogger.onDataGramReceived(received);

                if ((id != 0) && (received.getCon() != id)) {
                    throw new PTCPProtocolException("Received packet addressed to different connection id.");
                }

                sequence = received.getSeq();
                return received;
            } catch (SocketTimeoutException ste) {
                return null;
            } catch (IOException ex) {
                throw new PTCPException("IOException occured - send failed on socket.", ex);
            } catch (DeserializationException ex) {
                throw new PTCPException("Invalid binary data - cannot deserialize.", ex);
            }
        } else {
            throw new PTCPException("Tried to receive() when !isConnected()!");
        }
    }

    public boolean isConnected() {
        return (isConnecting() || (this.id != 0));
    }

    public void setConnectionType(PTCPConnectionType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public UnsignedShort getSequence() {
        return sequence;
    }

    public boolean isConnecting() {
        return connecting;
    }

    private void setConnecting(boolean connectiong) {
        this.connecting = connectiong;
    }

    /**
     * 
     * @param openRequest
     * @param openResponse
     * @return 
     */
    private boolean gotValidOpenResponse(PTCPOpenConnectionPacket openRequest, PTCPPacket openResponse) {
        if ((openRequest != null) && (openResponse != null)) {
            try {
                if (!openResponse.getFlag().equals(PTCPFlag.SYN)) {
                    System.out.println("Server responded with invalid flags! Expected SYN, got " + openResponse.getFlag().toString());
                    return false;
                }

                if (!openResponse.getAck().equals(new UnsignedShort(0)) || !openResponse.getSeq().equals(new UnsignedShort(0))) {
                    System.out.printf("Server responded with invalid ACK or SEQ value! Expected 0 and 0, got %s and %s\n", openResponse.getAck(), openResponse.getSeq());
                    return false;
                }

                if (!PTCPConnectionType.fromDataArray(openResponse.getData()).equals(type)) {
                    System.out.println("Server responded with SYN packet with command that does not match the requested command.");
                    return false;
                }

                if (openResponse.getCon() == 0) {
                    System.out.println("Server responded with SYN packet with zero connection id.");
                    return false;
                }

                return true;
            } catch (Exception ex) {
                System.out.println(CommandLine.formatException(ex));
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%d", ((int) id) & 0xffff);
    }
}
