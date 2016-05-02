/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.amaze;

import cz.sge.Game;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author merxbj
 */
public class MazeGame extends Game {

    @Override
    public void init() {
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(200, 200, 50, 50);
    }

    @Override
    public void update() {
        
    }

    @Override
    public void uninit() {
    }
    
}
