/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author merxbj
 */
public class GameField {

    private Snake snake;
    private Vector fieldSize;
    private Vector graphicSize;
    private final int FRAMES_PER_MOVEMENT = 100;
    private int framesCount;

    public GameField(int width, int height) {
        this.fieldSize = new Vector(width, height);
        this.graphicSize = new Vector();
    }
    
    public void assignSnake(Snake snake) {
        this.snake = snake;
    }

    public void init() {
        this.framesCount = 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, graphicSize.getX(), graphicSize.getY());

        int fieldBlockWidth = graphicSize.getX() / fieldSize.getX();
        int fieldBlockHeight = graphicSize.getY() / fieldSize.getY();

        g.setColor(Color.black);
        for (int y = 0; y < fieldSize.getY(); y++) {
            g.drawLine(0, y * fieldBlockHeight, graphicSize.getX(), y * fieldBlockHeight);
        }

        for (int x = 0; x < fieldSize.getX(); x++) {
            g.drawLine(x * fieldBlockWidth, 0, x * fieldBlockWidth, graphicSize.getY());
        }
        
        snake.draw();
    }

    public void uninit() {
    }

    public UpdateResult update() {
        framesCount++;
        if ((framesCount % FRAMES_PER_MOVEMENT) == 0) {
            if (!willSnakeHitTheWall()) {
                snake.move();
            } else {
                return UpdateResult.GameOver;
            }
        }
        return UpdateResult.Continue;
    }

    private boolean willSnakeHitTheWall() {
        return false;
    }
    
    public enum UpdateResult {
        Continue, GameOver
    }

    public void setGraphicWidth(int width) {
        this.graphicSize.setX(width);
    }
    
    public void setGraphicHeight(int height) {
        this.graphicSize.setY(height);
    }
    
}
