/*
 * GameApplication
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

import java.awt.Insets;
import javax.swing.JFrame;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class GameApplication {
    
    public static void start(Game game) {
        
        GameCanvas canvas = new GameCanvas(game);
        canvas.setDoubleBuffered(true);
        
        JFrame mainFrame = new JFrame(game.getTitle());
        mainFrame.setSize(800, 600);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(canvas);
        mainFrame.setVisible(true);
        
        // Make sure that the inner area of the frame have enough room to paint the game
        Insets insets = mainFrame.getInsets();
        int insetwidth = insets.left + insets.right;
        int insetheight = insets.top + insets.bottom;
        mainFrame.setSize(game.getWidth() + insetwidth, game.getHeight() + insetheight); 

        GameLoop loop = new GameLoop(game, canvas);
        loop.start();

        System.exit(0);
    }
}
