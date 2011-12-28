/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.statemachine;

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.PsiTP4Connection;
import cz.cvut.fel.psi.udp.core.PsiTP4Flag;
import cz.cvut.fel.psi.udp.core.PsiTP4Packet;
import cz.cvut.fel.psi.udp.core.SlidingOutboundWindow;
import cz.cvut.fel.psi.udp.core.exception.ConnectionResetException;
import cz.cvut.fel.psi.udp.core.exception.ProtocolException;
import cz.cvut.fel.psi.udp.core.exception.PsiTP4Exception;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author merxbj
 */
public class FileUploadState implements TransmissionState {

    private static final int SLIDING_ACK_RECEIVE_TIMEOUT_MILI = 100;
    private static final long SLIDING_ACK_RECEIVE_TIMEOUT_NANO = 100 * 1000000L;
    private static final long SAME_ACK_RECEIVED_MAX_COUNT = 3;
    //private static final int OPEN_CONNECTION_ATTEMPTS = 20;
    
    private int lastReceivedAck = 0;
    private int sameAckReceivedCount = 0;

    public TransmissionState process(StateMachine machine) throws ConnectionResetException {

        PsiTP4Connection connection = machine.getConnection();
        if (connection == null) {
            return new TransmissionFailedState(this);
        }

        try {

            SlidingOutboundWindow window = new SlidingOutboundWindow(openFileStream(machine.getTransmissionFileName()), machine.getSink());
            window.init();

            while (!window.isEmpty()) {
                sendCurrentWindow(window, connection);
                acceptAcknoledgements(window, connection);
            }

            machine.getSink().onTransferCompleted(0); // TODO: Fix the size here!
            return new RemoteSideDisconnectedState();
        } catch (ConnectionResetException ex) {
            throw ex;
        } catch (ProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            try {
                connection.reset();
            } catch (PsiTP4Exception ex) {
            }
        } catch (PsiTP4Exception ex) {
            System.out.println(CommandLine.formatException(ex));
        }

        return new TransmissionFailedState(this);
    }

    private void checkFlags(PsiTP4Flag psiTP4Flag) throws PsiTP4Exception {
        if (psiTP4Flag.equals(PsiTP4Flag.RST)) {
            throw new ConnectionResetException("Error occured during the transmission! Exiting...");
        } else if (!psiTP4Flag.equals(PsiTP4Flag.NONE)) {
            throw new PsiTP4Exception("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private InputStream openFileStream(String fileName) throws PsiTP4Exception {
        try {
            return new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            throw new PsiTP4Exception("Unable to open the firmware file!", ex);
        }
    }

    private void sendCurrentWindow(SlidingOutboundWindow window, PsiTP4Connection connection) throws PsiTP4Exception {
        // let's fill up the pipeline
        for (PsiTP4Packet dataPacket : window) {
            connection.send(dataPacket);
        }
    }

    private void acceptAcknoledgements(SlidingOutboundWindow window, PsiTP4Connection connection) throws PsiTP4Exception {
        long waitingForSlideBeginTime = System.nanoTime();

        boolean windowSlided = false;
        boolean timeoutExpired = false;
        while (!windowSlided && !timeoutExpired) {
            windowSlided = acceptIncomingPacket(window, connection);
            timeoutExpired = (System.nanoTime() - waitingForSlideBeginTime) >= SLIDING_ACK_RECEIVE_TIMEOUT_NANO;
        }
    }

    private boolean sameAckReceivedTooManyTimes(short ack, long maxTimes) {
        if (lastReceivedAck != ack) {
            lastReceivedAck = ack;
            sameAckReceivedCount = 0;
            return false;
        } else {
            return (++sameAckReceivedCount == maxTimes);
        }
    }

    private boolean acceptIncomingPacket(SlidingOutboundWindow window, PsiTP4Connection connection) throws PsiTP4Exception {
        PsiTP4Packet ackPacket = connection.receive(SLIDING_ACK_RECEIVE_TIMEOUT_MILI);
        if (ackPacket != null) {
            checkFlags(ackPacket.getFlag());
            if (sameAckReceivedTooManyTimes(ackPacket.getAck(), SAME_ACK_RECEIVED_MAX_COUNT)) {
                sendNextPacket(ackPacket.getAck(), window, connection);
            }
            return window.acknowledged(ackPacket.getAck());
        }
        return false;
    }

    private void sendNextPacket(short ack, SlidingOutboundWindow window, PsiTP4Connection connection) {
        connection
    }
}
