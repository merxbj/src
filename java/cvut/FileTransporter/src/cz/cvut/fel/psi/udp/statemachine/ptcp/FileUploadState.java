/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.statemachine.ptcp;

import cz.cvut.fel.psi.udp.application.CommandLine;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.PTCPConstants;
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

    private String firmwareFileName;
    private UnsignedShort lastReceivedAck = new UnsignedShort(0);
    private int sameAckReceivedCount = 0;
    private PTCPOutboundSlidingWindow window;

    public FileUploadState(String firmwareFileName) {
        this.firmwareFileName = firmwareFileName;
        this.window = new PTCPOutboundSlidingWindow();
    }

    public StateTransitionStatus process(Context context) {

        try {
            window.init(openFileStream());

            while (!window.isEmpty()) {
                sendCurrentWindow();
                acceptAcknoledgements();
            }

            return context.doStateTransition(new FileUploadFinishedState(window.getEnd()));
        } catch (PTCPProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            return context.doStateTransition(new TransmissionAbortedState());
        } catch (PTCPException ex) {
            System.out.println(CommandLine.formatException(ex));
        } finally {
            window.finish();
        }

        return context.doStateTransition(new TransmissionFailedState());
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

    private void sendCurrentWindow() throws PTCPException {
        // let's fill up the pipeline
        for (PTCPPacket dataPacket : window) {
            connection.send(dataPacket);
        }
    }

    private void acceptAcknoledgements() throws PTCPException {
        long waitingForSlideBeginTime = System.nanoTime();

        boolean windowSlided = false;
        boolean timeoutExpired = false;
        while (!windowSlided && !timeoutExpired) {
            windowSlided = acceptIncomingPacket();
            timeoutExpired = (System.nanoTime() - waitingForSlideBeginTime) >= PTCPConstants.UPLOAD_WINDOW_SLIDE_TIMEOUT_NANO;
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

    private boolean acceptIncomingPacket() throws PTCPException {
        PTCPPacket ackPacket = connection.receive();
        if (ackPacket != null) {
            checkFlags(ackPacket.getFlag());
            if (sameAckReceivedTooManyTimes(ackPacket.getAck(), PTCPConstants.SAME_ACK_RECEIVED_MAX_COUNT)) {
                sendNextPacket(ackPacket.getAck());
            }
            return window.acknowledged(ackPacket.getAck());
        }
        return false;
    }

    private void sendNextPacket(UnsignedShort ack) throws PTCPException {
        PTCPPacket packet = window.getPacketBySequence(ack);
        if (packet != null) {
            connection.send(packet);
        }
    }
}
