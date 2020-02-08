/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author merxbj
 */
public class Snake {
    
    private Element head;
    private Vector nextDirection;
    private Element tail;
    private Element tailTrail;
    private GameField field;
    private SnakeHeadAnimation headAnimation;

    public Snake(int headX, int headY) {
        this.head = new Element(new Vector(headX, headY), new Vector());
        this.tail = head;
        this.tailTrail = new Element(tail);
        this.headAnimation = new SnakeHeadAnimation();
        this.nextDirection = new Vector();
    }
    
    public void turnUp() {
        if (this.head.getDirection().getY() != 1) {
            this.nextDirection.setX(0);
            this.nextDirection.setY(-1);
        }
    }

    public void turnDown() {
        if (this.head.getDirection().getY() != -1) {
            this.nextDirection.setX(0);
            this.nextDirection.setY(1);
        }
    }

    public void turnLeft() {
        if (this.head.getDirection().getX() != 1) {
            this.nextDirection.setX(-1);
            this.nextDirection.setY(0);
        }
    }

    public void turnRight() {
        if (this.head.getDirection().getX() != -1) {
            this.nextDirection.setX(1);
            this.nextDirection.setY(0);
        }
    }

    public void init() {
        this.turnRight();
        this.grow(); // add regular tail
    }

    public void uninit() {
        // nothing to do right now
    }

    public GameField getField() {
        return field;
    }

    public void setField(GameField field) {
        this.field = field;
    }

    public void move() {
        Element newTail = tail.getNext();
        Element newHead = cutTheTail();
        
        tailTrail.setPosition(new Vector(newHead.getPosition()));
        tailTrail.setDirection(new Vector(newHead.getDirection()));
        
        newHead.setPosition(head.getPosition().add(nextDirection));
        newHead.setDirection(new Vector(nextDirection));
        
        this.head.prepend(newHead); // i think this is smart :-)
        this.head = newHead;
        this.tail = newTail;
    }
    
    public Vector getHeadPosition() {
        return this.head.getPosition();
    }
    
    public void grow() {
        this.grow(new Element());
    }
    
    private void grow(Element newTail) {
        tail.append(newTail);
        newTail.setDirection(tailTrail.getDirection());
        newTail.setPosition(tailTrail.getPosition());
        this.tail = newTail;
    }
    
    public void draw(Graphics g) {
        Color lastColor = g.getColor();
        Element current = head;
        while (current != null) {
            Rectangle rect = field.getFieldRectangle(current.getPosition());
            if (current == head) {
                drawHead(g, rect);
            } else {
                drawBody(g, rect);
            }
            current = current.previous;
        }
        g.setColor(lastColor);
    }

    private Element cutTheTail() {
        Element strip = this.tail.cutTail();
        this.tail = null;
        return strip;
    }

    public boolean isSnake(Vector pos) {
        Element current = head;
        while (current != null) {
            if (current.getPosition().equals(pos)) {
                return true;
            }
            current = current.previous;
        }
        return false;
    }

    private void drawHead(Graphics g, Rectangle rect) {
        headAnimation.draw(g, rect, head.getDirection());
    }

    private void drawBody(Graphics g, Rectangle rect) {
        g.setColor(Color.BLUE);
        g.fillRect(rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1);
    }

    public void updateAnimations() {
        headAnimation.update();
    }

    public void intoxicate() {
        headAnimation.setIntoxicated(true);
    }
    
    public void hitTheWall() {
        headAnimation.setHitTheWall(true);
    }

    public Vector getNextDirection() {
        return nextDirection;
    }

    private class Element {

        private Vector position;
        private Vector direction;
        private Element next;
        private Element previous;

        public Element() {
            this(new Vector(), new Vector());            
        }

        public Element(Vector position, Vector direction) {
            this.position = position;
            this.direction = direction;
            this.next = null;
            this.previous = null;
        }

        /**
         * Shallow copy of the snake element
         * @param copy 
         */
        public Element(Element copy) {
            this.position = new Vector(copy.position);
            this.direction = new Vector(copy.direction);
            this.next = copy.next;
            this.previous = copy.previous;
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

        public Element getNext() {
            return next;
        }

        public Element getPrevious() {
            return previous;
        }

        public Element append(Element tail) {
            if (this.previous == null) {
                this.previous = tail;
                tail.next = this;
                tail.previous = null;
                return tail;
            } else {
                throw new RuntimeException("You should never append in the middle of the snake!");
            }
        }
        
        public Element prepend(Element head) {
            if (this.next == null) {
                this.next = head;
                head.previous = this;
                head.next = null;
                return head;
            } else {
                throw new RuntimeException("You should never prepend in the middle of the snake!");
            }
        }
        
        public Element cutTail() {
            if (this.previous == null) {
                if (this.next != null) {
                    this.next.previous = null;
                    this.next = null;
                }
                return this;
            } else {
                throw new RuntimeException("This is not a tail you are cutting!");
            }
        }
    }
}
