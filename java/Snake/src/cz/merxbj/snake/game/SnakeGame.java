/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import cz.sge.Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 *
 * @author merxbj
 */
public class SnakeGame extends Game {

    private Snake snake;
    private GameField field;
    private boolean snakeIsDead;

    public SnakeGame() {
        createGame();
    }
    
    @Override
    public void draw(Graphics g) {
        this.field.draw(g);
        if (snakeIsDead) {
            Color attenuation = new Color(0, 0, 0, 70);
            g.setColor(attenuation);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 80));
            g.drawString("Game Over!", 90, 250);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
            g.drawString("Hit 'Q' to exit, 'R' to restart ...", 55, 300);
        } else if (isPaused()) {
            Color attenuation = new Color(0, 0, 0, 190);
            g.setColor(attenuation);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 70));
            g.drawString("Game Paused ...", 70, 250);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
            g.drawString("Hit space to continue ...", 20, 300);
        }
    }

    @Override
    public void init() {
        this.snakeIsDead = false;
        snake.setField(field);
        snake.init();
        field.setSnake(snake);
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
        if (!snakeIsDead) {
            GameField.UpdateResult result = field.update();
            switch (result) {
                case Continue:
                    break;
                case SnakeDead:
                    this.snakeIsDead = true;
                    break;
            }
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
            case KeyEvent.VK_R:
                if (snakeIsDead) {
                    this.restart();
                }
                break;
            case KeyEvent.VK_Q:
                this.over = true;
                break;
        }
    }
    
    private void restart() {
        uninit();
        createGame();
        init();
    }

    private void createGame() {
        snake = new Snake(9, 9);
        field = new GameField(20, 20);
    }
    
}
