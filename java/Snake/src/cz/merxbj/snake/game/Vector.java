/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

/**
 *
 * @author merxbj
 */
public class Vector {
    private int x;
    private int y;

    public Vector() {
        this(0,0);
    }
    
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y);
    }
    
}
