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
    
    public Vector(Vector copy) {
        this(copy.x, copy.y);
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
    
    public Vector subtract(Vector v) {
        return add(v.negative());
    }
    
    public int scalar(Vector v) {
        return this.x * v.x + this.y * v.y;
    }
    
    public Vector negative() {
        return new Vector(-this.x, -this.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector other = (Vector) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.x;
        hash = 61 * hash + this.y;
        return hash;
    }
}
