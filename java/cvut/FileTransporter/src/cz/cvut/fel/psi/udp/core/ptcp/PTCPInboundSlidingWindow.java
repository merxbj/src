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
package cz.cvut.fel.psi.udp.core.ptcp;

import cz.cvut.fel.psi.udp.core.SlidingWindow;
import cz.cvut.fel.psi.udp.core.UnsignedShort;
import cz.cvut.fel.psi.udp.core.ptcp.exception.PTCPException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class PTCPInboundSlidingWindow extends SlidingWindow {

    protected SortedMap<UnsignedShort, byte[]> currentWindow;
    protected OutputStream data;

    public PTCPInboundSlidingWindow() {
        super(new UnsignedShort(0));
        this.end = begin.add(size);
        this.currentWindow = new TreeMap<UnsignedShort, byte[]>();
        this.data = null;
    }

    public void init(UnsignedShort begin, OutputStream data) {
        this.begin = begin;
        this.data = data;
    }

    public boolean accept(byte[] chunk, UnsignedShort seq) {
        if (fitsToWindow(seq)) {
            currentWindow.put(seq, chunk);
            return true;
        }
        return false;
    }

    public boolean slideWindow() throws PTCPException {
        try {
            long slided = 0;

            while (currentWindow.containsKey(this.begin)) {
                byte[] chunk = currentWindow.remove(this.begin);
                data.write(chunk);
                begin = begin.add(chunk.length);
                end = end.add(chunk.length);
                slided += chunk.length;
            }

            if (slided > 0) {
                progressLogger.onWindowSlide(slided);
                return true;
            }
        } catch (IOException ex) {
            throw new PTCPException("Unable to write the confirmed data to the given stream!", ex);
        }

        return false;
    }

    protected boolean fitsToWindow(UnsignedShort seq) {
        /*
         * The additive constant will move the renge round the overflow-circle
         * to ensure the invariant that begin < end
         */
        UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
        UnsignedShort _begin = begin.add(offset);
        UnsignedShort _end = end.add(offset);
        UnsignedShort _seq = seq.add(offset);

        return (_seq.greaterThanOrEquals(_begin) && _seq.lessThan(_end));
    }

    public void finish() {
        try {
            if (data != null) {
                data.flush();
                data.close();
            }
        } catch (IOException ex) {
        }
    }
}
