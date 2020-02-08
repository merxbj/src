/*
 * OpenConnectionPacket
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

import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.exception.SerializationException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PTCPOpenConnectionPacket extends PTCPPacket {

    public PTCPOpenConnectionPacket(PTCPConnectionType type) throws SerializationException {
        this.setCon(0);
        this.setSeq(new UnsignedShort(0));
        this.setAck(new UnsignedShort(0));
        this.setFlag(PTCPFlag.SYN);
        this.setData(type.toDataArray());
    }
}
