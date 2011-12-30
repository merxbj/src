/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.psi.udp.core.ptcp;

import cz.cvut.fel.psi.udp.core.exception.DeserializationException;
import cz.cvut.fel.psi.udp.core.exception.SerializationException;

/**
 *
 * @author merxbj
 */
public enum PTCPConnectionType {

    UNDETERMINED,
    DOWNLOAD,
    UPLOAD;

    public byte[] toDataArray() throws SerializationException {
        byte[] dataArray = new byte[1];
        switch (this) {
            case DOWNLOAD:
                dataArray[0] = 0x1;
                break;
            case UPLOAD:
                dataArray[0] = 0x2;
                break;
            default:
                throw new SerializationException("Unexpected connection type information.");
        }
        return dataArray;
    }

    public static PTCPConnectionType fromDataArray(byte[] dataArray) throws DeserializationException {
        if (dataArray.length > 1) {
            throw new DeserializationException("Unexpected length of pseudo-data holding the connection type information.");
        }

        switch (dataArray[0]) {
            case 0x1:
                return PTCPConnectionType.DOWNLOAD;
            case 0x2:
                return PTCPConnectionType.UPLOAD;
            default:
                throw new DeserializationException("Unexpected pseudo-data value holding the connection type information.");
        }
    }
}
