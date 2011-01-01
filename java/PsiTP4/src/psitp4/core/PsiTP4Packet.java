/*
 * PsiTP4Packet
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

package psitp4.core;

import java.util.Arrays;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class PsiTP4Packet {

    private int con;
    private short seq;
    private short ack;
    private PsiTP4Flag flag;
    private byte[] data;

    public PsiTP4Packet() {
        this.con = 0;
        this.seq = 0;
        this.ack = 0;
        this.flag = PsiTP4Flag.NONE;
        this.data = new byte[0];
    }

    public void deserialize(byte[] data) throws DeserializationException {

        this.con = 0;
        for (int i = 0; i < 4; i++) {
            this.con += data[i];
            this.con <<= 1;
        }

        this.seq = 0;
        for (int i = 4; i < 6; i++) {
            this.seq += data[i];
            this.seq <<= 1;
        }

        this.ack = 0;
        for (int i = 6; i < 8; i++) {
            this.ack += data[i];
            this.ack <<= 1;
        }

        this.flag = PsiTP4Flag.deserialize(data[8]);

        this.data = Arrays.copyOfRange(data, 9, data.length);
        
    }

    public byte[] serialize() {
        byte[] d = new byte[9 + this.data.length];
        
        for (int i = 0; i < 2; i++) {
            d[i] = this.con
        }

        return d;
    }

    public short getAck() {
        return ack;
    }

    public void setAck(short ack) {
        this.ack = ack;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public PsiTP4Flag getFlag() {
        return flag;
    }

    public void setFlag(PsiTP4Flag flag) {
        this.flag = flag;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

}
