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
import java.util.SortedMap;
import java.util.TreeMap;
import cz.cvut.fel.psi.udp.application.ProgressSink;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SlidingWindow {
    private short begin;
    private short end;
    private short size;
    private SortedMap<Short, byte[]> currentWindow;
    private List<Byte> data;
    private ProgressSink sink;

    public SlidingWindow(short begin, short size, ProgressSink sink) {
        this.begin = begin;
        this.end = (short) (begin + size);
        this.size = size;
        this.currentWindow = new TreeMap<Short, byte[]>();
        this.data = new ArrayList<Byte>();
        this.sink = sink;
    }

    public short push(byte[] chunk, short seq) {
        if (fitsToWindow(seq)) {
            currentWindow.put(seq, chunk);
            if (slideWidnow()) {
                wipeWindow();
            }
        }
        return begin;
    }

    private boolean slideWidnow() {
        short oldBegin = this.begin;
        while (currentWindow.containsKey(this.begin)) {
            byte[] chunk = currentWindow.remove(this.begin);
            for (byte b : chunk) {
                data.add(b);
            }
            this.begin += chunk.length;
            this.end += chunk.length;
            sink.onWindowSlide(chunk.length);
        }
        return (this.begin != oldBegin);
    }

    private void wipeWindow() {
        List<Short> wiped = new ArrayList<Short>(currentWindow.size());
        for (Short s : currentWindow.keySet()) {
            if (!fitsToWindow(s)) {
                wiped.add(s);
            }
        }
        for (Short s : wiped) {
            currentWindow.remove(s);
        }
    }

    public byte[] pull() {
        byte[] d = new byte[data.size()];
        int i = 0;
        for (byte b : this.data) {
            d[i++] = b;
        }
        return d;
    }

    private boolean fitsToWindow(short seq) {
        /*
         * The following cast ensures unsigned comparsion of signed numbers.
         * The additive constant will move the renge round the overflow-circle
         * to ensure the invariant that begin < end
         */
        int offset = (((int) begin) & 0xffff) < (((int) end) & 0xffff) ? 0 : size;
        int _begin = ((int) (begin + offset)) & 0xffff;
        int _end = ((int) (end + offset)) & 0xffff;
        int _seq = ((int) (seq + offset)) & 0xffff;

        return ((_seq >= _begin) && (_seq <= _end));
    }

}
