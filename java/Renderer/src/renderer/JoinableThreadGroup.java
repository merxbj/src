/*
 * JoinableThreadGroup
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

package renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class JoinableThreadGroup extends ThreadGroup {

    public JoinableThreadGroup(String name) {
        super(name);
    }

    public void join() {
        Thread[] threads = new Thread[super.activeCount()];
        super.enumerate(threads);
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                // don't care
            }
        }
    }

}
