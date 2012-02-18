/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author merxbj
 */
public class Snake {
    
    private Deque<Element> elements;

    public Snake() {
        this.elements = new LinkedList<Element>();
    }
    
    public void turnUp() {
        this.head().direction.setX(0);
        this.head().direction.setY(-1);
    }

    public void turnDown() {
        this.head().direction.setX(0);
        this.head().direction.setY(1);
    }

    public void turnLeft() {
        this.head().direction.setX(-1);
        this.head().direction.setY(0);
    }

    public void turnRight() {
        this.head().direction.setX(1);
        this.head().direction.setY(0);
    }

    public void init() {
        this.turnRight();
    }

    public void uninit() {
        // nothing to do right now
    }

    public void move() {
        for (Element e : elements) {
            
        }
    }

    public Vector getHeadDirection() {
        return elements.peek().direction;
    }

    public Vector getHeadPosition() {
        return elements.peek().position;
    }
    
    private Element head() {
        return elements.peek();
    }
    
    private class Element {

        private Vector position;
        private Vector direction;

        public Element() {
            this(new Vector(), new Vector());
        }
        
        public Element(Vector position, Vector direction) {
            this.position = position;
            this.direction = direction;
        }

        public Vector getDirection() {
            return direction;
        }

        public void setDirection(Vector direction) {
            this.direction = direction;
        }

        public Vector getPosition() {
            return position;
        }

        public void setPosition(Vector position) {
            this.position = position;
        }
    }
}
