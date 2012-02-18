/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import java.awt.Graphics;

/**
 *
 * @author merxbj
 */
public class Field {

    private Snake snake;
    
    public void assignSnake(Snake snake) {
        this.snake = snake;
    }

    public void init() {
        
    }

    public void draw(Graphics g) {
        
    }

    public void uninit() {
        
    }

    public void update() {
        snake.move();
    }
    
}
