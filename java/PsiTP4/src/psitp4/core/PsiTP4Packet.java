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

import psitp4.core.exception.DeserializationException;
import psitp4.core.exception.SerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class PsiTP4Packet {

    public static int MAX_SIZE = 265; // header + data

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

    public void deserialize(byte[] data, int length) throws DeserializationException {

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data, 0, length));
        
        try {
            this.con = stream.readInt();
            this.seq = stream.readShort();
            this.ack = stream.readShort();
            this.flag = PsiTP4Flag.deserialize(stream);
            this.data = new byte[stream.available()];
            stream.read(this.data);
        } catch (IOException ex) {
            throw new DeserializationException("IOException thrown while deserializing!", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {}
        }
    }

    public byte[] serialize() throws SerializationException {
        
        byte[] bytes = new byte[9 + this.data.length];
        ByteArrayOutputStream bytesStream  = new ByteArrayOutputStream(bytes.length);
        DataOutputStream stream = new DataOutputStream(bytesStream);

        try {
            stream.writeInt(this.con);
            stream.writeShort(this.seq);
            stream.writeShort(this.ack);
            this.flag.serialize(stream);
            stream.write(this.data);
            bytes = bytesStream.toByteArray();
        } catch (IOException ex) {
            throw new SerializationException("IOException thrown while serializing!", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {}
        }

        return bytes;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
