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

    private TreeMap<UnsignedShort, Boolean> currentWindowAckStatus;
    private TreeMap<UnsignedShort, PTCPPacket> currentWindowCache;
    private InputStream data;

    public PTCPOutboundSlidingWindow(InputStream data) {
        super(new UnsignedShort(0));
        currentWindowAckStatus = new TreeMap<UnsignedShort, Boolean>();
        currentWindowCache = new TreeMap<UnsignedShort, PTCPPacket>();
        this.data = data;
    }

    public void init() {
        refill();
    }

    public boolean acknowledged(UnsignedShort ack) {
        if (currentWindowAckStatus.containsKey(ack)) {
            currentWindowAckStatus.put(ack, Boolean.TRUE);
            if (slideWindow()) {
                refill();
                return true;
            }
        }

        return false;
    }

    private boolean slideWindow() {
        List<PTCPPacket> ackedPackets = new ArrayList<PTCPPacket>(SLIDING_WINDOW_MAX_SIZE / PTCPPacket.MAX_DATA_SIZE);

        boolean acked = true;
        Iterator<Boolean> it = currentWindowAckStatus.values().iterator();
        while (it.hasNext() && acked) {
            acked = it.next();
            if (acked) {
                PTCPPacket ackedPacket = currentWindowCache.remove(begin);
                begin = begin.add(ackedPacket.getData().length);
                ackedPackets.add(ackedPacket);
            }
        }

        if (ackedPackets.size() > 0) {
            long totalAckedSize = 0;
            for (PTCPPacket ackedPacket : ackedPackets) {
                totalAckedSize += ackedPacket.getData().length;
                if (!currentWindowAckStatus.firstKey().equals(ackedPacket.getSeq().add(ackedPacket.getData().length))) {
                    throw new RuntimeException("This should have never happened! There is something wrong with the sequencing!");
                }
                currentWindowAckStatus.remove(currentWindowAckStatus.firstKey());
            }
            progressLogger.onWindowSlide(totalAckedSize);
            return true;
        }

        return false;
    }

    private void refill() {
        try {
            while ((data.available() > 0) && !isWindowFilled()) {
                byte[] chunk = new byte[PTCPPacket.MAX_DATA_SIZE];
                int len = data.read(chunk);
                UnsignedShort seq = end.add(1);
                PTCPDataUploadPacket packet = new PTCPDataUploadPacket(seq, chunk);
                currentWindowCache.put(seq, packet);
                currentWindowAckStatus.put(seq.add(len + 1), Boolean.FALSE);
                end = end.add(len);
            }
        } catch (IOException ex) {
        }
    }

    public Iterator<PTCPPacket> iterator() {
        return currentWindowCache.values().iterator();
    }

    public void close() {
        try {
            data.close();
        } catch (IOException ex) {
        }
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        try {
            isEmpty = (currentWindowCache.isEmpty() && (data.available() == 0));
        } catch (IOException ex) {
        }
        return isEmpty;
    }

    /**
     * TODO: Make this somehow shared (c&p from base class wtf????), maybe by new Class?
     * @return 
     */
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
}
