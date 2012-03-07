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
 * @author jmerxbauer
 */
public class SnakeHeadAnimation {
    
    private int mouthAngle;
    private boolean openingMouth;
    private boolean intoxicated;
    private boolean hitTheWall;

    public SnakeHeadAnimation() {
        this.mouthAngle = 90; // open wide baby!
        this.openingMouth = false;
        this.intoxicated = false;
        this.hitTheWall = false;
    }

    public void draw(Graphics g, Rectangle rect, Vector direction) {
        if (intoxicated) {
            g.setColor(Color.GREEN);
        } else if (hitTheWall) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        
        double mouthAxisRadians = Math.atan2(0 - direction.getY(), direction.getX());
        double mouthAxisDegrees = ((mouthAxisRadians > 0) ? mouthAxisRadians : (2 * Math.PI + mouthAxisRadians)) * 360 / (2 * Math.PI);
        double startMouthAngle = (mouthAxisDegrees + mouthAngle / 2) % 360;
        int mouthAngularExtend = 360 - mouthAngle;
        g.fillArc(rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1, (int) startMouthAngle, mouthAngularExtend);
    }

    public void update() {
        if (hitTheWall) {
            mouthAngle = 180; // smashed the wall!
        } else if (!intoxicated) {
            openingMouth = (openingMouth ? ((mouthAngle += 2) < 90) : ((mouthAngle -= 2) == 0)); // this is tricky muehehe!
        }
    }

    public boolean isHitTheWall() {
        return hitTheWall;
    }

    public void setHitTheWall(boolean hitTheWall) {
        this.hitTheWall = hitTheWall;
    }

    public boolean isIntoxicated() {
        return intoxicated;
    }

    public void setIntoxicated(boolean intoxicated) {
        this.intoxicated = intoxicated;
    }
}
