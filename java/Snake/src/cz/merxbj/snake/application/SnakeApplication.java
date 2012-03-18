/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.application;

import cz.merxbj.snake.game.SnakeGame;
import cz.sge.GameApplication;

/**
 *
 * @author merxbj
 */
public class SnakeApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SnakeGame snake = new SnakeGame();
        GameApplication.start(snake);
    }
}
