/*
 * Worm
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
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Worm implements Drawable {

    protected Hatchery hatch;
    protected Vector pos;
    protected Direction dir;
    protected int speed;

    public Worm() {
        this(null);
    }

    public Worm(Hatchery hatch) {
        this.hatch = hatch;
        if (hatch != null) {
            int x = (int) Math.floor(Math.random() * this.hatch.getSize().height);
            int y = (int) Math.floor(Math.random() * this.hatch.getSize().width);
            this.pos = new Vector(x,y);
        } else {
            this.pos = new Vector(0,0);
        }
        this.speed = 1;
        this.dir = Direction.getRandom();
    }

    public void attach(Hatchery hatch) {
        this.hatch = hatch;
    }

    public void draw(Graphics g) {
        g.setColor(Color.red);
        /*
        if (dir == Direction.East || dir == Direction.West) {
            g.fillRect(pos.x, pos.y, 5, 3);
        } else {
            g.fillRect(pos.x, pos.y, 3, 5);
        }
        */
        g.drawString(dir.toString(), pos.x, pos.y);
    }

    public void move() {
        move(dir.toVector().multiple(speed));
    }

    private void move(Vector vec) {
        this.pos = pos.add(vec);
    }

    public void update() {
        
        correctCollision();

        move();

        if (Math.random() > 0.99) {
            Direction newDir = Direction.getRandom();
            while (newDir == dir) {
                newDir = Direction.getRandom();
            }
            dir = newDir;
        }
    }

    private void correctCollision() {
        List<Direction> forbidden = new LinkedList<Direction>();
        if (pos.x == 5) {
            forbidden.add(Direction.West);
        } else if (pos.x == this.hatch.getSize().width - 6) {
            forbidden.add(Direction.East);
        }

        if (pos.y == 5) {
            forbidden.add(Direction.North);
        } else if (pos.y == this.hatch.getSize().height - 6) {
            forbidden.add(Direction.South);
        }

        while (forbidden.contains(dir)) {
            dir = Direction.getRandom();
        }

    }

}
