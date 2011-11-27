/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA 
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */
package DA.TreeMap;

/**
 * A JDK 1.0 - compatible rectangle class that
 * accepts double-valued parameters.
 */
public class Rect {

    public double x, y, width, height;

    public Rect() {
        this(0, 0, 1, 1);
    }

    public Rect(Rect r) {
        setRect(r.x, r.y, r.width, r.height);
    }

    public Rect(double x, double y, double width, double height) {
        setRect(x, y, width, height);
    }

    private void setRect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double aspectRatio() {
        return Math.max(width / height, height / width);
    }

    public double distance(Rect rectangle) {
        return Math.sqrt((rectangle.x - x) * (rectangle.x - x)
                + (rectangle.y - y) * (rectangle.y - y)
                + (rectangle.width - width) * (rectangle.width - width)
                + (rectangle.height - height) * (rectangle.height - height));
    }

    public Rect copy() {
        return new Rect(x, y, width, height);
    }
    
    public boolean contains(int pointerX, int pointerY) {
        return ( ( (pointerX > x) && (pointerX <= x+width) ) && 
                 ( (pointerY > y) && (pointerY <= y+height) ) );
    }

    @Override
    public String toString() {
        return "Rect: " + x + ", " + y + ", " + width + ", " + height;
    }
}
