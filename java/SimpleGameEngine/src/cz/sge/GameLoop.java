/*
 * GameLoop
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
package cz.sge;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class GameLoop extends Thread {

    private Game game;
    private GameCanvas canvas;

    public GameLoop(Game game, GameCanvas canvas) {
        this.game = game;
        this.canvas = canvas;
    }

    @Override
    public synchronized void start() {

        game.init();

        while (!game.isOver()) {
            long beginTime = System.nanoTime();

            if (!game.isPaused()) {
                game.update();
            }

            canvas.repaint();

            long timeTaken = System.nanoTime() - beginTime;
            long timeLeft = (game.getUpdatePeriod() - timeTaken) / 1000000;
            if (timeLeft < 10) {
                timeLeft = 10; // set minimum
            }
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) {
            }
        }

        game.uninit();
    }
}
