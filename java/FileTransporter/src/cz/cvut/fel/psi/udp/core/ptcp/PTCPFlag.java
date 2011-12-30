/*
 * PsiTP4Flag
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

import cz.cvut.fel.psi.udp.core.exception.DeserializationException;
import cz.cvut.fel.psi.udp.core.exception.SerializationException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Jaroslav Merxbauer
 */
public enum PTCPFlag {

    NONE,
    SYN,
    FIN,
    RST;

    public static PTCPFlag deserialize(InputStream stream) throws DeserializationException {
        DataInputStream in = new DataInputStream(stream);
        try {
            byte raw = (byte) (in.readByte() & 7); // mask out 5 leading values - they are not signficant
            switch (raw) {
                case 0:
                    return NONE;
                case 1:
                    return SYN;
                case 2:
                    return FIN;
                case 4:
                    return RST;
                default:
                    throw new DeserializationException("Flags are mixed together! It is allowed to have only one flag set!");
            }
        } catch (IOException ex) {
            throw new DeserializationException("IOException thrown while deserializing the flag!", ex);
        }
    }

    public void serialize(OutputStream stream) throws SerializationException {
        DataOutputStream out = new DataOutputStream(stream);
        try {
            switch (this) {
                case NONE:
                    out.write(0);
                    break;
                case SYN:
                    out.write(1);
                    break;
                case RST:
                    out.write(4);
                    break;
                case FIN:
                    out.write(2);
                    break;
                default:
                    throw new SerializationException("Unusable flag used! Cannot serialize!");
            }
        } catch (IOException ex) {
            throw new SerializationException("IOException thrown while serializing the flag!", ex);
        }
    }
}
