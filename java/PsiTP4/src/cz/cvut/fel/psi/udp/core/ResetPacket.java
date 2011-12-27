/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.core;

/**
 *
 * @author merxbj
 */
public class ResetPacket extends PsiTP4Packet {

    public ResetPacket(int con) {
        setCon(con);
        setFlag(PsiTP4Flag.RST);
    }
    
}
