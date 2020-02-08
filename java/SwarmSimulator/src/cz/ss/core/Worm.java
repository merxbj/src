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

package cz.ss.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
    protected Worm victim;
    protected Worm attacker;
    protected int cooldown;
    protected LinkedList<Worm> children;
    protected LinkedList<Worm> parents;
    protected long lifeTime;
    protected boolean isDying;
    boolean selected;

    public Worm() {
        this(null);
    }

    public Worm(Hatchery hatch) {
        if (hatch != null) {
            int x = (int) Math.floor(Math.random() * hatch.getSize().width - 5);
            int y = (int) Math.floor(Math.random() * hatch.getSize().height - 5);
            init(hatch, new Vector(x, y));
        } else {
            init(hatch, new Vector(0,0));
        }
    }

    public Worm(Hatchery hatch, Vector pos) {
        init(hatch, new Vector(pos.x, pos.y));
    }

    private void init(Hatchery hatch, Vector pos) {
        this.hatch = hatch;
        this.pos = pos;
        this.speed = 1;
        this.cooldown = 0;
        this.children = new LinkedList<Worm>();
        this.parents = new LinkedList<Worm>();
        this.lifeTime = 2000;
        this.isDying = false;
        
        this.selected = false;
        changeDirection(Direction.getRandom());
    }

    public void attach(Hatchery hatch) {
        this.hatch = hatch;
    }

    public void draw(Graphics2D g) {
        if (selected) {
            g.setColor(Color.yellow);
            g.drawRect(pos.x -1, pos.y - 1, this.size.width + 1, this.size.height + 1);
        }
        
        g.setColor(getColor());
        g.fillRect(pos.x, pos.y, this.size.width, this.size.height);

        if (attacker != null) {
            //g.setColor(Color.YELLOW);
            //g.draw(new Ellipse2D.Double(pos.x - 3, pos.y - 3, 10, 10));
        } else if (victim != null) {
            //g.setColor(Color.red);
            //g.drawLine(pos.x, pos.y, victim.pos.x, victim.pos.y);
        }

        for (Worm parent : parents) {
            //g.setColor(Color.green);
            //g.drawLine(pos.x, pos.y, parent.pos.x, parent.pos.y);
        }
    }

    public void move() {
        move(dir.toVector().multiple(speed));
    }

    private void move(Vector vec) {
        if (vec != null) {
            this.pos = pos.add(vec);
        }
    }

    public void update() {
        
        checkInvariants();

        if (cooldown > 0) {
            cooldown--;
        }

        correctCollision();

        move();

        if (!chaseAnotherWorm()) {

            changeRandomlyDirection();

        }

        lifeTime--;
        if (lifeTime == 0) {
            hatch.died(this);
        } else if (lifeTime <= 100) {
            isDying = true;
        }

    }

    private void correctCollision() {
        List<Direction> forbidden = new LinkedList<Direction>();
        if (pos.x <= 0) {
            forbidden.add(Direction.West);
            forbidden.add(Direction.NorthWest);
            forbidden.add(Direction.SouthWest);
        } else if (pos.x >= (this.hatch.getSize().width - 5)) {
            forbidden.add(Direction.East);
            forbidden.add(Direction.NorthEast);
            forbidden.add(Direction.SouthEast);
        }

        if (pos.y <= 0) {
            forbidden.add(Direction.North);
            forbidden.add(Direction.NorthWest);
            forbidden.add(Direction.NorthEast);
        } else if (pos.y >= (this.hatch.getSize().height - 5)) {
            forbidden.add(Direction.South);
            forbidden.add(Direction.SouthWest);
            forbidden.add(Direction.SouthEast);
        }

        while (forbidden.contains(dir)) {
            changeDirection(Direction.getRandom());
        }

    }

    private void changeDirection(Direction newDir) {
        this.dir = newDir;
        if (dir == Direction.East || dir == Direction.West) {
            this.size = new Dimension(15, 9);
        } else {
            this.size = new Dimension(9, 15);
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
        //    throw new AssertionError(String.format("The Worm is out of horiznotal bounds: %s !", this.pos));
        }

        if ((this.pos.y < 0) || (this.pos.y > (this.hatch.getSize().height - this.size.height))) {
          //  throw new AssertionError(String.format("The Worm is out of vertical bounds: %s !", this.pos));
        }

    }

    protected Color getColor() {
        return Color.BLACK;
    }

    private boolean chaseAnotherWorm() {
        //boolean b = false;if (!b) return b;

        if (attacker != null || cooldown > 0) {
            return false;
        }

        if (victim == null) {
            victim = lookForVictim();
        }

        if (victim != null) {
            Vector dist = this.pos.substract(victim.pos).abs();
            if (dist.x < 3 && dist.y < 3) {
                if (this.getClass().equals(victim.getClass())) {
                    this.hatch.died(victim);
                    this.victim = null;
                    this.speed = 1;
                    this.cooldown = 250;
                    return false;
                } else {
                    double lucky = Math.random();
                    int childCount = (lucky < 0.99) ? 1 : (lucky < 0.999) ? 2 : (lucky < 0.9999) ? 3 : 4;
                    for (; --childCount >= 0;) {
                        Worm child = (Math.random() > 0.5) ? new FemaleWorm(hatch, pos) : new MaleWorm(hatch, pos);
                        this.children.add(child);
                        this.victim.children.add(child);
                        child.registerParent(this);
                        child.registerParent(victim);
                        this.hatch.breed(child);
                    }
                    this.victim = null;
                    this.speed = 1;
                    this.cooldown = 100;
                }
            } else {
                Vector dirVect = victim.pos.substract(this.pos).toDirectionVector();
                this.changeDirection(Direction.fromVector(dirVect));
                victim.setAttacker(this);
                this.speed = 1;
                return true;
            }
        }
        
        return false;
    }

    private Worm lookForVictim() {
        for (Worm w : hatch.getSwarm()) {
            Vector distance = w.pos.substract(this.pos).abs();
            if (distance.x < 50 && distance.y < 50) {
                if (canAttack(w)) {
                    return w;
                }
            }
        }
        return null;
    }

    protected void setAttacker(Worm attacker) {
        this.attacker = attacker;
    }

    public void onDied() {
        if (this.victim != null) {
            this.victim.setAttacker(null);
        }

        if (this.attacker != null) {
            this.attacker.victim = null;
        }

        for (Worm parent : parents) {
            parent.children.remove(this);
        }

        for (Worm child : children) {
            child.parents.remove(this);
        }

        this.parents.clear();
        this.children.clear();
    }

    protected void registerParent(Worm parent) {
        this.parents.add(parent);
    }

    public void detach() {
        this.hatch = null;
    }

    private boolean canAttack(Worm w) {
        if (!this.equals(w) && !this.children.contains(w) && !this.parents.contains(w)) {
            if (this.parents.size() > 0) {
                for (Worm potentialSiblink : this.parents.get(0).children) {
                    if (w.equals(potentialSiblink)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
