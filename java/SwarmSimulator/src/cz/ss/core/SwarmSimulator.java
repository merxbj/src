/*
 * SwarmSimulator
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

import cz.sge.Game;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class SwarmSimulator extends Game {

    private Hatchery hatchery;

    public SwarmSimulator() {
        this.title = "Swarm Simulator";
        this.hatchery = new Hatchery();
        this.hatchery.setSize(new Dimension(width, height));
    }

    @Override
    public void init() {
        this.hatchery.init();
    }

    @Override
    public void update() {
        this.hatchery.update();
    }

    @Override
    public void draw(Graphics g) {
        this.hatchery.draw((Graphics2D) g);
    }

    @Override
    public void uninit() {
        this.hatchery.clear();
        
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        hatchery.onClick(me.getX(), me.getY());
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                togglePaused();
                break;
            case KeyEvent.VK_Q:
                over = true;
                break;
        }
    }

}
