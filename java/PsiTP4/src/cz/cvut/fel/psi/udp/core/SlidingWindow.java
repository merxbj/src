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

import cz.cvut.fel.psi.udp.application.ProgressSink;

/**
 * TODO: Think about this once more again - abstract class isn't maybe the best
 * idea - there is too few shared functionality and meaning :-(
 * @author eTeR
 * @version %I% %G%
 */
public abstract class SlidingWindow {

    public static final short SLIDING_WINDOW_MAX_SIZE = 2048;
    protected short begin;
    protected short end;
    protected short size;
    protected ProgressSink sink;

    public SlidingWindow(short begin, ProgressSink sink) {
        this.size = SLIDING_WINDOW_MAX_SIZE;
        this.begin = begin;
        this.end = begin; // the abstract window is by default closed
        this.sink = sink;
    }

    protected boolean fitsToWindow(short seq) {
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
