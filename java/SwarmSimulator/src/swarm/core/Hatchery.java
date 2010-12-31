/*
 * Hatchery
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

package swarm.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Hatchery implements Drawable {

    private List<Worm> swarm;
    private Dimension size;

    public Hatchery() {
        this.swarm = new LinkedList<Worm>();
    }

    public void init() {
        int initialWormCount = 20; // TODO: Configurable!

        for (; initialWormCount-- > 0;) {
            Worm w = new Worm(this); // breed worm at random pos
            this.breed(w);
        }
    }

    public void clear() {
        swarm.clear();
    }

    private void breed(Worm w) {
        this.swarm.add(w);
    }

    public void draw(Graphics g) {
        
        g.clearRect(0, 0, size.width, size.height);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, size.height);

        for (Drawable d : swarm) {
            d.draw(g);
        }
    }

    public void update() {
        for (Worm w : swarm) {
            w.update();
        }
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

}
