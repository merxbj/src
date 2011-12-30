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
package cz.cvut.fel.psi.udp.core.ptcp;

import cz.cvut.fel.psi.udp.core.Packet;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.exception.DeserializationException;
import cz.cvut.fel.psi.udp.core.exception.SerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class PTCPPacket implements Packet {

    private int con;
    private UnsignedShort seq;
    private UnsignedShort ack;
    private PTCPFlag flag;
    private byte[] data;

    public PTCPPacket() {
        this.con = 0;
        this.seq = new UnsignedShort(0);
        this.ack = new UnsignedShort(0);
        this.flag = PTCPFlag.NONE;
        this.data = new byte[0];
    }

    public void deserialize(byte[] data, int length) throws DeserializationException {

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data, 0, length));

        try {
            this.con = stream.readInt();
            this.seq = new UnsignedShort(stream.readShort());
            this.ack = new UnsignedShort(stream.readShort());
            this.flag = PTCPFlag.deserialize(stream);
            this.data = new byte[stream.available()];
            stream.read(this.data);
        } catch (IOException ex) {
            throw new DeserializationException("IOException thrown while deserializing!", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
            }
        }
    }

    public byte[] serialize() throws SerializationException {

        byte[] bytes = new byte[9 + this.data.length];
        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream(bytes.length);
        DataOutputStream stream = new DataOutputStream(bytesStream);

        try {
            stream.writeInt(this.con);
            stream.writeShort(this.seq.getShortValue());
            stream.writeShort(this.ack.getShortValue());
            this.flag.serialize(stream);
            stream.write(this.data);
            bytes = bytesStream.toByteArray();
        } catch (IOException ex) {
            throw new SerializationException("IOException thrown while serializing!", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
            }
        }

        return bytes;
    }

    public UnsignedShort getAck() {
        return ack;
    }

    public void setAck(UnsignedShort ack) {
        this.ack = ack;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public PTCPFlag getFlag() {
        return flag;
    }

    public void setFlag(PTCPFlag flag) {
        this.flag = flag;
    }

    public UnsignedShort getSeq() {
        return seq;
    }

    public void setSeq(UnsignedShort seq) {
        this.seq = seq;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (data.length > PTCPConstants.PACKET_MAX_DATA_SIZE) {
            throw new RuntimeException("Unexpected data size exceeding the MAX_DATA_SIZE!");
        }

        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("con=0x%08X, seq=%s, ack=%s, flg=%s, sze=%d", con, seq, ack, flag, data.length);
    }
}
