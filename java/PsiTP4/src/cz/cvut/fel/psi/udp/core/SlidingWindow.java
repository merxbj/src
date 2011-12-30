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

import cz.cvut.fel.psi.udp.application.ProgressLogger;
import cz.cvut.fel.psi.udp.application.ProgressLoggerFactory;

/**
 * 
 * @author eTeR
 * @version %I% %G%
 */
public abstract class SlidingWindow {

    public static final short SLIDING_WINDOW_MAX_SIZE = 2048;
    protected UnsignedShort begin;
    protected UnsignedShort end;
    protected UnsignedShort size;
    protected ProgressLogger progressLogger;

    public SlidingWindow(UnsignedShort begin) {
        this.size = new UnsignedShort(SLIDING_WINDOW_MAX_SIZE);
        this.begin = begin;
        this.end = begin;  // the abstract window is by default closed
        this.progressLogger = ProgressLoggerFactory.getLogger();
    }

    public UnsignedShort getBegin() {
        return new UnsignedShort(begin);
    }

    public UnsignedShort getEnd() {
        return new UnsignedShort(end);
    }

    public UnsignedShort getSize() {
        return new UnsignedShort(size);
    }

    public final class Boundraies {

        private UnsignedShort begin;
        private UnsignedShort end;
        private UnsignedShort offset;
        
        public Boundraies(UnsignedShort begin, UnsignedShort end) {
            this(begin, end, new UnsignedShort(0));
        }
        
        public Boundraies(UnsignedShort begin, UnsignedShort end, UnsignedShort offset) {
            this.begin = new UnsignedShort(begin);
            this.end = new UnsignedShort(end);
            this.offset = new UnsignedShort(offset);
        }

        public Boundraies normalize() {
            UnsignedShort _offset = begin.lessThanOrEquals(end) ? new UnsignedShort(0) : size;
            UnsignedShort _begin = begin.add(_offset);
            UnsignedShort _end = end.add(_offset);
            return new Boundraies(_begin, _end, _offset);
        }

        public UnsignedShort getBegin() {
            return new UnsignedShort(begin);
        }

        public void setBegin(UnsignedShort begin) {
            this.begin = new UnsignedShort(begin);
        }

        public UnsignedShort getEnd() {
            return new UnsignedShort(end);
        }

        public void setEnd(UnsignedShort end) {
            this.end = new UnsignedShort(end);
        }

        public UnsignedShort getOffset() {
            return new UnsignedShort(offset);
        }

        public void setOffset(UnsignedShort offset) {
            this.offset = new UnsignedShort(offset);
        }

    }
}
