/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.statemachine.ptcp;

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPFlag;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPPacket;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPOutboundSlidingWindow;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPConnectionResetException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPProtocolException;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import cz.cvut.fel.psi.udp.statemachine.Context;
import cz.cvut.fel.psi.udp.statemachine.StateTransitionStatus;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author merxbj
 */
public class FileUploadState extends PTCPState {

    private static final int SLIDING_ACK_RECEIVE_TIMEOUT_MILI = 100;
    private static final long SLIDING_ACK_RECEIVE_TIMEOUT_NANO = 100 * 1000000L;
    private static final long SAME_ACK_RECEIVED_MAX_COUNT = 3;
    //private static final int OPEN_CONNECTION_ATTEMPTS = 20;
    private String firmwareFileName;
    private UnsignedShort lastReceivedAck = new UnsignedShort(0);
    private int sameAckReceivedCount = 0;

    public FileUploadState(String firmwareFileName) {
        this.firmwareFileName = firmwareFileName;
    }

    public StateTransitionStatus process(Context context) {

        try {

            PTCPOutboundSlidingWindow window = new PTCPOutboundSlidingWindow(openFileStream());
            window.init();

            while (!window.isEmpty()) {
                sendCurrentWindow(window);
                acceptAcknoledgements(window);
            }

            return context.doStateTransition(new RemoteSideDisconnectedState());
        } catch (PTCPProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            try {
                connection.reset();
            } catch (PTCPException ex) {
            }
        } catch (PTCPException ex) {
            System.out.println(CommandLine.formatException(ex));
        }

        return context.doStateTransition(new TransmissionFailedState(this));
    }

    private void checkFlags(PTCPFlag psiTP4Flag) throws PTCPException {
        if (psiTP4Flag.equals(PTCPFlag.RST)) {
            throw new PTCPConnectionResetException();
        } else if (!psiTP4Flag.equals(PTCPFlag.NONE)) {
            throw new PTCPException("Protocol failure! Got unepxected flag during transmission...");
        }
    }

    private InputStream openFileStream() throws PTCPException {
        try {
            return new FileInputStream(firmwareFileName);
        } catch (FileNotFoundException ex) {
            throw new PTCPException("Unable to open the firmware file!", ex);
        }
    }

    private void sendCurrentWindow(PTCPOutboundSlidingWindow window) throws PTCPException {
        // let's fill up the pipeline
        for (PTCPPacket dataPacket : window) {
            connection.send(dataPacket);
        }
    }

    private void acceptAcknoledgements(PTCPOutboundSlidingWindow window) throws PTCPException {
        long waitingForSlideBeginTime = System.nanoTime();

        boolean windowSlided = false;
        boolean timeoutExpired = false;
        while (!windowSlided && !timeoutExpired) {
            windowSlided = acceptIncomingPacket(window);
            timeoutExpired = (System.nanoTime() - waitingForSlideBeginTime) >= SLIDING_ACK_RECEIVE_TIMEOUT_NANO;
        }
    }

    private boolean sameAckReceivedTooManyTimes(UnsignedShort ack, long maxTimes) {
        if (!lastReceivedAck.equals(ack)) {
            lastReceivedAck = ack;
            sameAckReceivedCount = 0;
            return false;
        } else {
            return (++sameAckReceivedCount == maxTimes);
        }
    }

    private boolean acceptIncomingPacket(PTCPOutboundSlidingWindow window) throws PTCPException {
        PTCPPacket ackPacket = connection.receive(SLIDING_ACK_RECEIVE_TIMEOUT_MILI);
        if (ackPacket != null) {
            checkFlags(ackPacket.getFlag());
            if (sameAckReceivedTooManyTimes(ackPacket.getAck(), SAME_ACK_RECEIVED_MAX_COUNT)) {
                sendNextPacket(ackPacket.getAck(), window);
            }
            return window.acknowledged(ackPacket.getAck());
        }
        return false;
    }

    private void sendNextPacket(UnsignedShort ack, PTCPOutboundSlidingWindow window) {
    }
}
