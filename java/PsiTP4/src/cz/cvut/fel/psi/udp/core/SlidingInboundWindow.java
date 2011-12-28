/*
 * SlidingWindow
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

import java.util.ArrayList;
import java.util.List;
import cz.cvut.fel.psi.udp.application.ProgressSink;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SlidingInboundWindow extends SlidingWindow {

    protected SortedMap<Short, byte[]> currentWindow;
    protected List<Byte> data;
    
    public SlidingInboundWindow(short begin, ProgressSink sink) {
        super(begin, sink);
        this.end = (short) (begin + size);
        this.currentWindow = new TreeMap<Short, byte[]>();
        this.data = new ArrayList<Byte>();
    }

    public boolean accept(byte[] chunk, short seq) {
        if (fitsToWindow(seq)) {
            currentWindow.put(seq, chunk);
            return true;
        }
        return false;
    }

    public boolean slideWindow() {
        long slided = 0;

        while (currentWindow.containsKey(this.begin)) {
            byte[] chunk = currentWindow.remove(this.begin);
            for (byte b : chunk) {
                data.add(b);
            }
            this.begin += chunk.length;
            this.end += chunk.length;
            slided += chunk.length;
        }

        if (slided > 0) {
            sink.onWindowSlide(slided);
            return true;
        }

        return false;
    }

    public byte[] getData() {
        byte[] d = new byte[data.size()];
        int i = 0;
        for (byte b : this.data) {
            d[i++] = b;
        }
        return d;
    }

    public short getBegin() {
        return begin;
    }

}
