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
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Hatchery implements Drawable {

    private Queue<Worm> swarm;
    private List<Worm> cemetery;
    private List<Worm> hive;
    private Dimension size;
    private Worm selected;

    public Hatchery() {
        this.swarm = new ConcurrentLinkedQueue();
        this.cemetery = new LinkedList<Worm>();
        this.hive = new LinkedList<Worm>();
        this.selected = null;
    }

    public void init() {
        this.clear();
        int initialWormCount = 20; // TODO: Configurable!
        for (; initialWormCount-- > 0;) {
            Worm w = (Math.random() > 0.5) ? new FemaleWorm(this) : new MaleWorm(this); // breed worm at random pos
            this.breed(w);
        }
    }

    public void clear() {
        swarm.clear();
        cemetery.clear();
        hive.clear();
        this.selected = null;
    }

    public void breed(Worm w) {
        this.hive.add(w);
    }

    public void died(Worm w) {
        this.cemetery.add(w);
    }

    public void draw(Graphics2D g) {
        
        g.clearRect(0, 0, size.width, size.height);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, size.width, size.height);

        g.setColor(Color.BLACK);
        g.drawRect(1, 1, size.width - 3, size.height - 3);

        for (Drawable d : swarm) {
            d.draw(g);
        }

        g.setColor(Color.white);
        g.drawString(String.format("Worms: %d", swarm.size()), 10, 10);
        g.drawString(selected != null ? String.format("%d", selected.lifeTime) : "", 10, 20);
    }

    public void update() {
        for (Worm larva : hive) {
            swarm.add(larva);
        }
        hive.clear();

        for (Worm worm : swarm) {
            worm.update();
        }

        for (Worm dead : cemetery) {
            dead.onDied();
            swarm.remove(dead);
            dead.detach();
        }
        cemetery.clear();
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Queue<Worm> getSwarm() {
        return swarm;
    }

    public void onClick(int x, int y) {
        for (Worm w : swarm) {
            if (((x >= w.pos.x) && (x <= (w.pos.x + w.size.width))) &&
                ((y >= w.pos.y) && (y <= (w.pos.y + w.size.height)))) {
                if (w != selected) {
                    w.selected = true;
                    if (selected != null) {
                        this.selected.selected = false;
                    }
                    this.selected = w;
                }
            }
        }
    }

}
