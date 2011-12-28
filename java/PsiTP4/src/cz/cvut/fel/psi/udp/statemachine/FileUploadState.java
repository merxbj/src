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
    //private static final int OPEN_CONNECTION_ATTEMPTS = 20;
    
    public TransmissionState process(StateMachine machine) throws ConnectionResetException {
        
        PsiTP4Connection connection = machine.getConnection();
        if (connection == null) {
            return new TransmissionFailedState(this);
        }

        try {
            
            SlidingOutboundWindow window = new SlidingOutboundWindow(openFileStream(machine.getTransmissionFileName()), machine.getSink());
            window.init();
            
            while (!window.isEmpty()) {

                // let's fill up the pipeline first
                for (PsiTP4Packet dataPacket : window) {
                    connection.send(dataPacket);
                }
                
                long waitingForSlideBeginTime = System.nanoTime();
                
                boolean windowSlided = false;
                boolean timeoutExpired = false;
                while (!windowSlided && !timeoutExpired) {
                    PsiTP4Packet ackPacket = connection.receive(SLIDING_ACK_RECEIVE_TIMEOUT_MILI);
                    if (ackPacket != null) {
                        checkFlags(ackPacket.getFlag());
                        windowSlided = window.acknowledged(ackPacket.getAck());
                    }
                    timeoutExpired = (System.nanoTime() - waitingForSlideBeginTime) >= SLIDING_ACK_RECEIVE_TIMEOUT_NANO;
                }
            }

            machine.getSink().onTransferCompleted(0); // TODO: Fix the size here!
            return new RemoteSideDisconnectedState();
        } catch (ConnectionResetException ex) {
            throw ex;
        } catch (ProtocolException pex) {
            System.out.println(CommandLine.formatException(pex));
            try {
                connection.reset();
            } catch (PsiTP4Exception ex) {}
        } 
        catch (PsiTP4Exception ex) {
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
    
}
