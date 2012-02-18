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
    private GameField field;

    public SnakeGame() {
        snake = new Snake();
        field = new GameField(20, 20);
    }
    
    @Override
    public void draw(Graphics g) {
        this.field.draw(g);
    }

    @Override
    public void init() {
        snake.init();
        field.assignSnake(snake);
        field.setGraphicHeight(height);
        field.setGraphicWidth(width);
        field.init();
    }

    @Override
    public void uninit() {
        field.uninit();
        snake.uninit();
    }

    @Override
    public void update() {
        GameField.UpdateResult result = field.update();
        switch (result) {
            case Continue:
                break;
            case GameOver:
                this.over = true;
                break;
        }
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
