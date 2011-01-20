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

package psitp4.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import psitp4.application.ProgressSink;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SlidingWindow {
    private short begin;
    private short end;
    private short size;
    private Map<Short, byte[]> currentWindow;
    private List<Byte> data;
    private ProgressSink sink;

    public SlidingWindow(short begin, short size, ProgressSink sink) {
        this.begin = begin;
        this.end = (short) (begin + size);
        this.size = size;
        this.currentWindow = new HashMap<Short, byte[]>();
        this.data = new ArrayList<Byte>();
        this.sink = sink;
    }

    public short push(byte[] chunk, short seq) {
        if (fitsToWindow(seq)) {
            currentWindow.put(seq, chunk);
            slideWidnow();
        }
        return begin;
    }

    private void slideWidnow() {
        System.out.println(String.format("wnd_before: begin=%d, end=%d", begin, end));
        while (currentWindow.containsKey(this.begin)) {
            byte[] chunk = currentWindow.remove(this.begin);
            for (byte b : chunk) {
                data.add(b);
            }
            this.begin += chunk.length;
            this.end += chunk.length;
            sink.onWindowSlide(chunk.length);
        }
        System.out.println(String.format("wnd_after: begin=%d, end=%d", begin, end));
        System.out.println(String.format("wnd: %d chunks remaining", currentWindow.size()));
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
        
        System.out.println(String.format("wnd: sanity_check: b=%d, e=%d, s=%d", _begin, _end, _seq));
        return ((_seq >= _begin) && (_seq <= _end));
    }

}
