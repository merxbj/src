/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.common;

import java.awt.Canvas;
import java.awt.Graphics;

/**
 *
 * @author mrneo
 */
public class MainCanvas extends Canvas {
    
    public MainCanvas() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        g.drawRect(100, 100, 100, 100);
    }
}
