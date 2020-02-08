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
    public MazeGame() {
        super();
        this.width = 1280;
        this.height = 760;
        this.padding = 50;
    }

    @Override
    public void init() {
        maze = new int[][] {
            {WALL, WALL, WALL, WALL, WALL, PATH, WALL, WALL, WALL, WALL, WALL},
            {WALL, PATH, PATH, PATH, PATH, PATH, PATH, PATH, WALL, PATH, WALL},
            {WALL, PATH, WALL, WALL, WALL, WALL, WALL, PATH, WALL, PATH, WALL},
            {WALL, PATH, WALL, PATH, PATH, PATH, WALL, PATH, PATH, PATH, WALL},
            {WALL, PATH, WALL, PATH, WALL, PATH, WALL, PATH, WALL, WALL, WALL},
            {WALL, PATH, WALL, PATH, WALL, PATH, WALL, PATH, PATH, PATH, WALL},
            {WALL, WALL, WALL, PATH, WALL, PATH, WALL, WALL, WALL, PATH, WALL},
            {WALL, PATH, PATH, PATH, WALL, PATH, PATH, PATH, PATH, PATH, WALL},
            {WALL, PATH, WALL, WALL, WALL, WALL, WALL, WALL, WALL, PATH, WALL},
            {WALL, PATH, PATH, PATH, PATH, PATH, WALL, PATH, PATH, PATH, WALL},
            {WALL, WALL, WALL, WALL, WALL, PATH, WALL, WALL, WALL, WALL, WALL},
        };
    }

    @Override
    public void draw(Graphics g) {
        int cellSize = (this.height - this.padding*2)/maze.length;
        g.setColor(Color.black);
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                if (maze[row][col] == WALL) {
                    g.fillRect(padding + cellSize*col, padding + cellSize*row, cellSize, cellSize);
                }
            }
        }
    }

    @Override
    public void update() {
        
    }

    @Override
    public void uninit() {
    }

    private int maze[][];
    private final int padding;
    private final int WALL = 0;
    private final int PATH = 1;
}
