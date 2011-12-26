/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psitp4.core;

import psitp4.core.exception.DeserializationException;

/**
 *
 * @author merxbj
 */
public enum PsiTP4ConnectionType {
    DOWNLOAD,
    UPLOAD;

    public byte[] toDataArray() {
        byte[] dataArray = new byte[1];
        switch (this) {
            case DOWNLOAD:
                dataArray[0] = 0x1;
                break;
            case UPLOAD:
                dataArray[0] = 0x2;
                break;
        }
        return dataArray;
    }
    
    public static PsiTP4ConnectionType fromDataArray(byte[] dataArray) throws DeserializationException {
        if (dataArray.length > 1) {
            throw new DeserializationException("Unexpected length of pseudo-data holding the connection type information.");
        }
        
        switch (dataArray[0]) {
            case 0x1:
                return PsiTP4ConnectionType.DOWNLOAD;
            case 0x2:
                return PsiTP4ConnectionType.UPLOAD;
            default:
                throw new DeserializationException("Unexpected pseudo-data value holding the connection type information.");
        }
    }
}
