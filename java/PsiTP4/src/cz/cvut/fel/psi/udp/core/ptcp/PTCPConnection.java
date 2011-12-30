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

    private InetAddress hostname;
    private int port;
    private DatagramSocket socket;
    private int id;
    private UnsignedShort closingSequence;
    private PTCPConnectionType type;
    private boolean connecting;
    private ProgressLogger progressLogger;
    private UnsignedShort lastSentSeq;
    private int sameSeqSentCount;

    public PTCPConnection(InetAddress hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.id = 0;
        this.closingSequence = new UnsignedShort(0);
        this.type = PTCPConnectionType.UNDETERMINED;
        this.progressLogger = ProgressLoggerFactory.getLogger();
        this.lastSentSeq = new UnsignedShort(0);
        this.sameSeqSentCount = 0;
    }

    public void open() throws PTCPException {

        if (isConnected()) {
            System.out.println("Attempted to open already opened connection. Nothing has been done.");
            return;
        }

        setConnecting(true);

        try {

            this.socket = new DatagramSocket();
            PTCPOpenConnectionPacket openRequest = new PTCPOpenConnectionPacket(type);
            PTCPPacket openResponse = null;

            boolean connectionEstablished = false;
            while (!connectionEstablished) {
                this.send(openRequest);
                openResponse = this.receive();
                if (gotValidOpenResponse(openRequest, openResponse)) {
                    connectionEstablished = true;
                }
            }

            if (!connectionEstablished) {
                throw new PTCPException("Failed to establish a new connection!");
            }

            this.id = openResponse.getCon();
            progressLogger.onConnectionOpen(this);

        } catch (PTCPException ex) {
            throw ex;
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

        PTCPFinishedPacket closeRequest = new PTCPFinishedPacket(closingSequence);
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

            if ((type == PTCPConnectionType.UPLOAD) && sameSeqSentTooManyTimes(packet.getSeq())) {
                throw new PTCPProtocolException("Tried to send the same sequence number too many times! Thats enough!");
            }

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
        if (isConnected()) {
            try {
                byte[] rcv = new byte[PTCPConstants.PACKET_MAX_SIZE];
                DatagramPacket response = new DatagramPacket(rcv, rcv.length);
                socket.setSoTimeout(PTCPConstants.RECEIVE_TIMEOUT_MILI);
                socket.receive(response);

                PTCPPacket received = new PTCPPacket();
                received.deserialize(response.getData(), response.getLength());

                progressLogger.onDataGramReceived(received);

                if ((id != 0) && (received.getCon() != id)) {
                    throw new PTCPProtocolException("Received packet addressed to different connection id.");
                }
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

    public void setClosingSequence(UnsignedShort closingSequence) {
        this.closingSequence = closingSequence;
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
        return String.format("0x%08X", id);
    }

    private boolean sameSeqSentTooManyTimes(UnsignedShort seq) {
        if (!lastSentSeq.equals(seq)) {
            lastSentSeq = new UnsignedShort(seq);
            sameSeqSentCount = 0;
            return false;
        } else {
            return (++sameSeqSentCount == PTCPConstants.SAME_SEQ_SENT_MAX_COUNT);
        }
    }
}
