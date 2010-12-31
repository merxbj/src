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
import java.awt.Dimension;
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
    protected Dimension size;
    protected int speed;

    public Worm() {
        this(null);
    }

    public Worm(Hatchery hatch) {
        this.hatch = hatch;
        if (hatch != null) {
            int x = (int) Math.floor(Math.random() * this.hatch.getSize().width - 5);
            int y = (int) Math.floor(Math.random() * this.hatch.getSize().height - 5);
            this.pos = new Vector(x,y);
        } else {
            this.pos = new Vector(0,0);
        }
        this.speed = 2;
        changeDirection(Direction.getRandom());
    }

    public void attach(Hatchery hatch) {
        this.hatch = hatch;
    }

    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(pos.x, pos.y, this.size.width, this.size.height);
    }

    public void move() {
        move(dir.toVector().multiple(speed));
    }

    private void move(Vector vec) {
        this.pos = pos.add(vec);
    }

    public void update() {
        
        checkInvariants();

        correctCollision();

        move();

        changeRandomlyDirection();

    }

    private void correctCollision() {
        List<Direction> forbidden = new LinkedList<Direction>();
        if (pos.x <= 0) {
            forbidden.add(Direction.West);
        } else if (pos.x >= this.hatch.getSize().width - 5) {
            forbidden.add(Direction.East);
        }

        if (pos.y <= 0) {
            forbidden.add(Direction.North);
        } else if (pos.y >= this.hatch.getSize().height - 5) {
            forbidden.add(Direction.South);
        }

        while (forbidden.contains(dir)) {
            changeDirection(Direction.getRandom());
        }

    }

    private void changeDirection(Direction newDir) {
        this.dir = newDir;
        if (dir == Direction.East || dir == Direction.West) {
            this.size = new Dimension(5, 3);
        } else {
            this.size = new Dimension(3, 5);
        }
    }

    private void changeRandomlyDirection() {
        if (Math.random() > 0.99) {
            Direction newDir = Direction.getRandom();
            while (newDir == dir) {
                newDir = Direction.getRandom();
            }

            changeDirection(newDir);
        }
    }

    private void checkInvariants() {
        
        if ((this.pos.x < 0) || (this.pos.x > (this.hatch.getSize().width - this.size.width))) {
            throw new AssertionError(String.format("The Worm is out of horiznotal bounds: %s !", this.pos));
        }

        if ((this.pos.y < 0) || (this.pos.y > (this.hatch.getSize().height - this.size.height))) {
            throw new AssertionError(String.format("The Worm is out of vertical bounds: %s !", this.pos));
        }
    }

}
