/*
 * GameCanvas
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

import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class GameCanvas extends JComponent {
    
    private Game game;

    public GameCanvas(Game game) {
        this.game = game;
        this.setSize(game.getWidth(), game.getHeight());
        this.addKeyListener(game);
        this.addMouseListener(game);
        this.addMouseMotionListener(game);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.draw(g);
    }

}
