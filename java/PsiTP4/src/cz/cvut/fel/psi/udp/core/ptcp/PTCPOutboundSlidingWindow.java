/*
 * SlidingOutboundWindow
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

import cz.cvut.fel.psi.udp.core.SlidingWindow;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class PTCPOutboundSlidingWindow extends SlidingWindow implements Iterable<PTCPPacket> {

    private TreeMap<UnsignedShort, PTCPPacket> currentWindowCache;
    private InputStream data;

    public PTCPOutboundSlidingWindow() {
        super(new UnsignedShort(0));
        currentWindowCache = new TreeMap<UnsignedShort, PTCPPacket>();
    }

    public void init(InputStream data) throws PTCPException {
        this.data = data;
        refill();
    }

    public boolean acknowledged(UnsignedShort ack) throws PTCPException {
        if (fitsToWindow(ack)) {
            if (slideWindow(ack)) {
                refill();
                return true;
            }
        }

        return false;
    }

    private boolean slideWindow(UnsignedShort ack) {
        List<PTCPPacket> ackedPackets = new ArrayList<PTCPPacket>(SLIDING_WINDOW_MAX_SIZE / PTCPConstants.PACKET_MAX_DATA_SIZE);

        Boundraies boundaries = new Boundraies(begin, end).normalize();
        while (boundaries.getBegin().lessThan(ack.add(boundaries.getOffset()))) {
            PTCPPacket ackedPacket = currentWindowCache.remove(begin);
            begin = begin.add(ackedPacket.getData().length);
            ackedPackets.add(ackedPacket);
            boundaries = new Boundraies(begin, end).normalize();
        }

        if (ackedPackets.size() > 0) {
            long totalAckedSize = 0;
            for (PTCPPacket ackedPacket : ackedPackets) {
                totalAckedSize += ackedPacket.getData().length;
            }
            progressLogger.onWindowSlide(totalAckedSize);
            return true;
        }

        return false;
    }

    private void refill() throws PTCPException {
        try {
            while ((data.available() > 0) && !isWindowFilled()) {
                byte[] chunk = new byte[PTCPConstants.PACKET_MAX_DATA_SIZE];
                int len = data.read(chunk);
                UnsignedShort seq = end;
                PTCPDataUploadPacket packet = new PTCPDataUploadPacket(seq, chunk, len);
                currentWindowCache.put(seq, packet);
                end = end.add(len);
            }
        } catch (IOException ex) {
            throw new PTCPException("Unable to read an additional data from the given stream!", ex);
        }
    }

    public Iterator<PTCPPacket> iterator() {
        return currentWindowCache.values().iterator();
    }
    
    public PTCPPacket getPacketBySequence(UnsignedShort seq) {
        return currentWindowCache.get(seq);
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        try {
            isEmpty = (currentWindowCache.isEmpty() && (data.available() == 0));
        } catch (IOException ex) {
        }
        return isEmpty;
    }

    protected boolean fitsToWindow(UnsignedShort ack) {
        /*
         * The additive constant will move the renge round the overflow-circle
         * to ensure the invariant that begin < end
         */
        UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
        UnsignedShort _begin = begin.add(offset);
        UnsignedShort _end = end.add(offset);
        UnsignedShort _ack = ack.add(offset);

        return (_ack.greaterThan(_begin) && _ack.lessThanOrEquals(_end));
    }

    private boolean isWindowFilled() {
        /*
         * The following cast ensures unsigned comparsion of signed numbers.
         * The additive constant will move the renge round the overflow-circle
         * to ensure the invariant that begin < end
         */
        UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
        UnsignedShort _begin = begin.add(offset);
        UnsignedShort _end = end.add(offset);

        return ((_end.substract(_begin)).equals(size));
    }
    
    public void finish() {
        try {
            if (data != null) {
                data.close();
            }
        } catch (IOException ex) {
            
        }
    }

}
