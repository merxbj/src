/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.snake.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author merxbj
 */
public class GameField {

    private Snake snake;
    private Vector fieldSize;
    private Vector graphicSize;
    private int framesPerMoevement;
    private int framesCount;
    private Vector foodPosition;
    private Vector poisonPosition;
    private Random random;
    private final int FOOD_TO_LEVEL_UP = 2;
    private final int POISON_MAX_LIFETIME_TICKS = 1000;
    private long foodEaten;
    private long poisonLifetime;

    public GameField(int width, int height) {
        this.fieldSize = new Vector(width, height);
        this.graphicSize = new Vector();
        this.foodPosition = new Vector(-1, -1);
        this.poisonPosition = new Vector(-1, -1);
        this.random = new Random(Calendar.getInstance().getTimeInMillis());
        this.foodEaten = 0;
        this.framesPerMoevement = 20;
    }
    
    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public void init() {
        this.framesCount = 0;
    }
    
    public UpdateResult update() {
        framesCount++;
        
        updateFood();
        updatePoison();        
        UpdateResult result = updateSnake();

        return result;
    }

    public void draw(Graphics g) {
        snake.updateAnimations();

        drawField(g);
        drawWalls(g);
        drawFood(g);
        drawPoison(g);

        snake.draw(g);
    }

    public void uninit() {
    }

    private boolean willSnakeHitTheWall() {
        Vector nextHeadPos = snake.getHeadPosition().add(snake.getNextDirection());
        return ((nextHeadPos.getX() < 1) || (nextHeadPos.getX() >= fieldSize.getX()) || 
                (nextHeadPos.getY() < 1) || (nextHeadPos.getY() >= fieldSize.getY()));
    }

    private void updateFood() {
        if ((foodPosition.getX() == -1) || (foodPosition.getY() == -1)) {
            do {
                this.foodPosition.setX(random.nextInt(fieldSize.getX() - 1) + 1);
                this.foodPosition.setY(random.nextInt(fieldSize.getY() - 1) + 1);
            } while (snake.isSnake(foodPosition));
        }
    }

    private boolean willSnakeHitHimself() {
        Vector nextHeadPos = snake.getHeadPosition().add(snake.getNextDirection());
        return this.snake.isSnake(nextHeadPos);
    }

    private boolean snakeEatenFood() {
        return snake.getHeadPosition().equals(this.foodPosition);
    }

    private void removeFood() {
        this.foodPosition.setX(-1);
        this.foodPosition.setY(-1);
    }

    private void updateDifficulty() {
        this.foodEaten++;
        if ((this.foodEaten % FOOD_TO_LEVEL_UP) == 0) {
            this.framesPerMoevement = Math.max(1, (int) Math.ceil(this.framesPerMoevement / 1.2));
        }
    }

    private void removePoison() {
        this.poisonPosition.setX(-1);
        this.poisonPosition.setY(-1);
    }

    private boolean snakeEatenPoison() {
        return snake.getHeadPosition().equals(this.poisonPosition);
    }

    private void updatePoison() {
        if ((poisonPosition.getX() == -1) || (poisonPosition.getY() == -1)) {
            if (random.nextDouble() > 0.998) {
                do {
                    this.poisonPosition.setX(random.nextInt(fieldSize.getX() - 1) + 1);
                    this.poisonPosition.setY(random.nextInt(fieldSize.getY() - 1) + 1);
                    this.poisonLifetime = POISON_MAX_LIFETIME_TICKS;
                } while (snake.isSnake(poisonPosition) || foodPosition.equals(poisonPosition));
            }
        } else {
            if (--poisonLifetime == 0) {
                removePoison();
            }
        }
    }

    private void drawField(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, graphicSize.getX(), graphicSize.getY());

        double fieldBlockWidth = graphicSize.getX() / (fieldSize.getX() + 1.0);
        double fieldBlockHeight = graphicSize.getY() / (fieldSize.getY() + 1.0);

        g.setColor(Color.black);
        for (int y = 0; y < (fieldSize.getY() + 1); y++) {
            g.drawLine(0, (int) (y * fieldBlockHeight), graphicSize.getX(), (int) (y * fieldBlockHeight));
        }

        for (int x = 0; x < (fieldSize.getX() + 1); x++) {
            g.drawLine((int) (x * fieldBlockWidth), 0, (int) (x * fieldBlockWidth), graphicSize.getY());
        }
    }

    private void drawFood(Graphics g) {
        if ((foodPosition.getX() != -1) && (foodPosition.getY() != -1)) {
            Rectangle food = getFieldRectangle(foodPosition);
            g.setColor(Color.MAGENTA);
            g.fillArc(food.x, food.y, food.width, food.height, 0, 360);
        }
    }

    private void drawPoison(Graphics g) {
        if ((poisonPosition.getX() != -1) && (poisonPosition.getY() != -1)) {
            Rectangle poison = getFieldRectangle(poisonPosition);
            g.setColor(Color.RED);
            g.fillArc(poison.x, poison.y, poison.width, poison.height, 0, 360);
        }
    }

    private void drawWalls(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int y = 0; y < fieldSize.getY() + 1; y++) {
            for (int x = 0; x < fieldSize.getX() + 1; x++) {
                if ((y == 0) || (y == fieldSize.getY()) ||
                     (x == 0) || (x == fieldSize.getX())) {
                    Rectangle rec = getFieldRectangle(new Vector(x, y));
                    g.fillRect(rec.x, rec.y, rec.width, rec.height);
                }
            }
        }
    }

    private UpdateResult updateSnake() {
        if ((framesCount % framesPerMoevement) == 0) {
            if (!willSnakeHitTheWall() && !willSnakeHitHimself()) {
                snake.move();
            } else {
                snake.hitTheWall();
                return UpdateResult.SnakeDead;
            }
            
            if (snakeEatenFood()) {
                snake.grow();
                removeFood();
                updateDifficulty();
            } else if (snakeEatenPoison()) {
                snake.intoxicate();
                removePoison();
                return UpdateResult.SnakeDead;
            }
        }
        
        return UpdateResult.Continue;
    }
    
    public enum UpdateResult {
        Continue, SnakeDead
    }

    public void setGraphicWidth(int width) {
        this.graphicSize.setX(width);
    }
    
    public void setGraphicHeight(int height) {
        this.graphicSize.setY(height);
    }
    
    public Rectangle getFieldRectangle(Vector field) {
        
        double fieldBlockWidth = graphicSize.getX() / (fieldSize.getX() + 1.0);
        double fieldBlockHeight = graphicSize.getY() / (fieldSize.getY() + 1.0);
        
        Rectangle rect = new Rectangle();
        rect.x = 0 + (int) (fieldBlockWidth * field.getX());
        rect.y = 0 + (int) (fieldBlockHeight * field.getY());
        rect.width = (int) fieldBlockWidth;
        rect.height = (int) fieldBlockHeight;
        
        return rect;
    }
}
