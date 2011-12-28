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
package cz.cvut.fel.psi.udp.core;

import cz.cvut.fel.psi.udp.application.ProgressSink;
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
public class SlidingOutboundWindow extends SlidingWindow implements Iterable<PsiTP4Packet> {

    private TreeMap<Short, Boolean> currentWindowAckStatus;
    private TreeMap<Short, PsiTP4Packet> currentWindowCache;
    private InputStream data;
    
    public SlidingOutboundWindow(InputStream data, ProgressSink sink) {
        super((short) 0, sink);
        this.end = (short) 0;
        currentWindowAckStatus = new TreeMap<Short, Boolean>();
        currentWindowCache = new TreeMap<Short, PsiTP4Packet>();
        this.data = data;
    }
    
    public void init() {
        refill();
    }
    
    public boolean acknowledged(short ack) {
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
        List<PsiTP4Packet> ackedPackets = new ArrayList<PsiTP4Packet>(SLIDING_WINDOW_MAX_SIZE / PsiTP4Packet.MAX_DATA_SIZE);
        
        boolean acked = true;
        Iterator<Boolean> it = currentWindowAckStatus.values().iterator();
        while (it.hasNext() && acked) {
            acked = it.next();
            if (acked) {
                PsiTP4Packet ackedPacket = currentWindowCache.remove(begin);
                begin += ackedPacket.getData().length;
                ackedPackets.add(ackedPacket);
            }
        }
        
        if (ackedPackets.size() > 0) {
            long totalAckedSize = 0;
            for (PsiTP4Packet ackedPacket : ackedPackets) {
                totalAckedSize += ackedPacket.getData().length;
                if (currentWindowAckStatus.firstKey() != ackedPacket.getSeq() + ackedPacket.getData().length) {
                    throw new RuntimeException("This should have never happened! There is something wrong with the sequencing!");
                }
                currentWindowAckStatus.remove(currentWindowAckStatus.firstKey());
            }
            sink.onWindowSlide(totalAckedSize);
            return true;
        }
        
        return false;
    }
    
    private void refill() {
        try {
            while ((data.available() > 0) && !isWindowFilled()) {
                byte[] chunk = new byte[PsiTP4Packet.MAX_DATA_SIZE];
                int len = data.read(chunk);
                short seq = (short) (end + 1);
                DataUploadPacket packet = new DataUploadPacket(seq, chunk);
                this.currentWindowCache.put(seq, packet);
                this.currentWindowAckStatus.put((short) (seq + len + 1), Boolean.FALSE);
                this.end += len;
            }
        } catch (IOException ex) {
            
        }
    }

    public Iterator<PsiTP4Packet> iterator() {
        return currentWindowCache.values().iterator();
    }
    
    public void close() {
        try {
            this.data.close();
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
        int offset = (((int) begin) & 0xffff) < (((int) end) & 0xffff) ? 0 : size;
        int _begin = ((int) (begin + offset)) & 0xffff;
        int _end = ((int) (end + offset)) & 0xffff;
        
        return ((_end - _begin) == SLIDING_WINDOW_MAX_SIZE);
    }
}
