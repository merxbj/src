/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.core.ptcp;

/**
 *
 * @author merxbj
 */
public class PTCPResetPacket extends PTCPPacket {

    public PTCPResetPacket(int con) {
        setCon(con);
        setFlag(PTCPFlag.RST);
    }
}
