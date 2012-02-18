/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import cz.sge.Game;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 *
 * @author merxbj
 */
public class SnakeGame extends Game {

    private Snake snake;
    private Field field;

    public SnakeGame() {
        snake = new Snake();
        field = new Field();
    }
    
    @Override
    public void draw(Graphics g) {
        this.field.draw(g);
    }

    @Override
    public void init() {
        snake.init();
        field.assignSnake(snake);
        field.init();
    }

    @Override
    public void uninit() {
        field.uninit();
        snake.uninit();
    }

    @Override
    public void update() {
        field.update();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                this.paused = !this.paused;
                break;
            case KeyEvent.VK_UP:
                this.snake.turnUp();
                break;
            case KeyEvent.VK_DOWN:
                this.snake.turnDown();
                break;
            case KeyEvent.VK_LEFT:
                this.snake.turnLeft();
                break;
            case KeyEvent.VK_RIGHT:
                this.snake.turnRight();
                break;
        }
    }
    
}
