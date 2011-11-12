/*
 * TMAlgorithm.java
 * www.bouthier.net
 *
 * The MIT License :
 * -----------------
 * Copyright (c) 2001 Christophe Bouthier, Vesselin Markovsky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package DSA.TreeMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Observable;

/**
 * The TMAlgorithm abstract class represent the algorithm
 * of the TreeMap. It should be subclassed by every class
 * that want to implement a particular treemap (classic treemap,
 * squarified treemap, ...).
 * <P>
 * A subclass can also override the drawNode() method
 * to have a customized drawing of a node.
 * <P>
 * And now with cushion treemap, thanks to Jarke J. van Wijk :-)
 *
 * @author Christophe Bouthier [bouthier@loria.fr]
 * @author Vesselin Markovsky [markovsky@semantec.bg]
 * @version 2.5
 */
public abstract class TMAlgorithm extends Observable {

    private static final ColorModel cModel = ColorModel.getRGBdefault();
    private static final int dimMax = 32;
    private static WritableRaster[][] cachedRasters = new WritableRaster[dimMax + 1][dimMax + 1];
    private static int[][][] cachedBuffers = new int[dimMax + 1][dimMax + 1][];
    //   private TMCushionPaint painter;
    private final Font titleFont = new Font("Dialog", Font.PLAIN, 10);
    private final Color borderColor = Color.black;
    protected double h = 0.50;
    protected double f = 1;
    protected boolean cushion = false;
    protected boolean border = true;
    protected int IS = 215;
    protected final double LX = 0.09759;
    protected final double LY = -0.19518;
    protected final double LZ = 0.9759;
    // Axis of separation in the TreeMap
    protected final static short HORIZONTAL = 0;
    protected final static short VERTICAL = 1;
    protected TMNodeModel root = null; // root of the TMNodeModel tree
    protected TreeMapView view = null; // view using this TMAlgorithm
    protected int borderSize = 2; // size of the border
    protected int borderLimit = 0; // limit to draw nested border
    protected boolean nodesTitles = true; // draw the nodes titles.


    /* --- Initialization --- */
    /**
     * Initialize the algorithm.
     *
     * @param root     the root of the TMNodeModel tree
     * @param view     the view using this algorithm
     */
    void initialize(TMNodeModel root, TreeMapView view) {
        this.root = root;
        this.view = view;
    }


    /* --- Nodes titles --- */
    /**
     * Draws the nodes titles.
     */
    public void drawNodesTitles() {
        setDrawingTitles(true);
    }

    /**
     * Don't draws the nodes titles.
     */
    public void dontDrawNodesTitles() {
        setDrawingTitles(false);
    }

    public boolean isDrawingTitles() {
        return nodesTitles;
    }

    public void setDrawingTitles(boolean drawing) {
        nodesTitles = drawing;
        view.repaint();
        setChanged();
        notifyObservers();
    }


    /* --- Cushion management --- */
    public void setH(double h) {
        this.h = h;
        view.repaint();
        setChanged();
        notifyObservers();
    }

    public double getH() {
        return h;
    }

    public void setF(double f) {
        this.f = f;
        view.repaint();
        setChanged();
        notifyObservers();
    }

    public double getF() {
        return f;
    }

    public void setIS(int IS) {
        this.IS = IS;
        view.repaint();
        setChanged();
        notifyObservers();
    }

    public int getIS() {
        return IS;
    }


    /* --- Nested management --- */
    /**
     * Sets the border size.
     *
     * @param size    the border size
     */
    public void setBorderSize(int size) {
        borderSize = size;
        borderLimit = (borderSize * 2) + 4;
        view.repaint();
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the border size.
     *
     * @return    the border size
     */
    public int getBorderSize() {
        return borderSize;
    }
    
    /* --- Drawing --- */
    /**
     * Starts the process of drawing the treemap.
     *
     * @param g       the graphic context
     * @param root    the root
     */
    void draw(Graphics2D g, TMNodeModel root) {
        this.root = root;
        drawNodes(g, root, HORIZONTAL, 1);
    }

    /**
     * Draws the node and recurses the drawing on its children.
     *
     * @param g        the graphic context
     * @param node     the node to draw
     * @param axis     the axis of separation
     * @param level    the level of deep
     */
    protected void drawNodes(Graphics2D g, TMNodeModel node, short axis, int level) {
        Rectangle oldClip = g.getClipBounds();
        Rectangle area = node.getArea();
        g.clipRect(area.x, area.y, area.width + 1, area.height + 1);
        fillNode(g, node, level);

        if (!node.isLeaf()) {
            drawChildren(g, (TMNodeModelComposite) node, axis, level);
        } else {
        }
        g.setClip(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
    }


    /* --- SubClass utility --- */
    /**
     * Switch the axis and return the new axis.
     *
     * @param axis    the axis to switch
     * @return        the new axis
     */
    protected short switchAxis(short axis) {
        // Axis : Bold as love
        if (axis == HORIZONTAL) {
            return VERTICAL;
        } else {
            return HORIZONTAL;
        }
    }

    /* --- COULD BE OVERRIDED IN SUBCLASS --- */
    /**
     * Fills the node.
     *
     * @param g        the graphic context
     * @param node     the TMNodeModel to draw
     * @param level    the level of deep
     */
    protected void fillNode(Graphics2D g, TMNodeModel node, int level) {
        Rectangle area = node.getArea();

        // WARNING !!!
        // Don't use g.fill(Shape s) or g.draw(Shape s),
        // they are really too slow !!!
        g.setPaint(node.getFilling());
        g.fillRect(area.x, area.y, area.width, area.height);
        g.setPaint(borderColor);
        g.drawRect(area.x, area.y, area.width, area.height);
        if (nodesTitles) {
            g.setPaint(node.getColorTitle());
            g.setFont(titleFont);
            g.drawString(node.getTitle(), area.x + 1, area.y + 10);
        }
    }


    /* --- TO BE IMPLEMENTED IN SUBCLASS --- */
    /**
     * Draws the children of a node, by setting their drawing area first,
     * dependant of the algorithm used.
     *
     * @param g        the graphic context
     * @param node     the node whose children should be drawn
     * @param axis     the axis of separation
     * @param level    the level of deep
     */
    protected abstract void drawChildren(Graphics2D g, TMNodeModelComposite node, short axis, int level);
}
