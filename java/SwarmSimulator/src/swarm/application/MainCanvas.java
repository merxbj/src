/*
 * MainCanvas
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

package swarm.application;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import swarm.core.Hatchery;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class MainCanvas extends Canvas implements Runnable {

    private static final int FPS = 60;
    private static final long UPDATE_PERIOD = 1000000000L / FPS;

    private Hatchery hatch;
    private boolean quit;
    private Image offscreen;
    private Graphics2D backBuffer;

    public MainCanvas() {
        this.quit = true;
        this.offscreen = null;
        this.backBuffer = null;
    }

    @Override
    public void paint(Graphics g) {

        if (this.hatch == null || this.backBuffer == null) {
            return;
        }

        this.hatch.draw(backBuffer);
        g.drawImage(offscreen, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void setHatch(Hatchery hatch) {
        this.hatch = hatch;
        this.hatch.setSize(this.getSize());
    }

    public synchronized void setQuit(boolean quit) {
        this.quit = quit;
    }

    public synchronized boolean getQuit() {
        return quit;
    }

    public void run() {

        this.offscreen = createImage(getSize().width, getSize().height);
        this.backBuffer = (Graphics2D) offscreen.getGraphics();
        this.hatch.init();

        long beginTime;
        long timeTaken;
        long timeLeft;

        while (!getQuit()) {

            beginTime = System.nanoTime();

            updateHatchery();
            repaint();

            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000;
            if (timeLeft < 10) {
                timeLeft = 10; // set minimum
            }
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) {}
        }

    }

    private void updateHatchery() {
        hatch.update();
    }

    void onClick(MouseEvent me) {
        hatch.onClick(me.getX(), me.getY());
    }

}
