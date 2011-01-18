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
    private Map<Short, byte[]> currentWindow;
    private List<Byte> data;
    private ProgressSink sink;

    public SlidingWindow(short size, ProgressSink sink) {
        this.begin = 0;
        this.end = size;
        this.currentWindow = new HashMap<Short, byte[]>();
        this.data = new ArrayList<Byte>();
    }

    public short push(byte[] chunk, short seq) {
        currentWindow.put(seq, chunk);
        slideWidnow();
        return begin;
    }

    private void slideWidnow() {
        while (currentWindow.containsKey(this.begin)) {
            byte[] chunk = currentWindow.remove(this.begin);
            for (byte b : chunk) {
                data.add(b);
            }
            this.begin += chunk.length;
            sink.onWindowSlide(chunk.length);
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

}
