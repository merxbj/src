/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.common;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mrneo
 */
public class SimpleFile implements Iterable<SimpleFile>, Comparable<SimpleFile> {
    private String name;
    private String path;
    private List<SimpleFile> content;
    private long directorySize = 0;
    private long size = 0;
    private Rectangle bounds;
    private int depth;
    private boolean hasContent = false;
    
    public SimpleFile(File folder) {
        this.name = folder.getName();
        this.path = folder.getAbsolutePath();
        this.directorySize = folder.length();
        this.bounds = new Rectangle();
    }

    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void add(SimpleFile so) {
        if (content == null) {
             content = new ArrayList<SimpleFile>();
        }
        this.content.add(so);
    }

    public long getSize() {
        size = 0;
        if (content != null && !content.isEmpty()) {
            for (SimpleFile o : content) {
                size += o.getSize();
            }
        }
        return size + directorySize;
    }
    
    public List<SimpleFile> getContent() {
        return content;
    }
    
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public Iterator<SimpleFile> iterator() {
        return content.iterator();
    }

    public void draw(Graphics2D g, int x, int y, int width, int height) {
        /*if (content != null && !content.isEmpty()) {
            MapLayout algorithm = new SquarifiedLayout();
            algorithm.layout(content, new Rect(x, y, width, height));

            for (int i = 0; i < content.size(); i++) {
                Rect rect = content.get(i).getBounds();
                if (rect != null) {
                    int a = (int) rect.x;
                    int b = (int) rect.y;
                    int c = (int) (rect.x + rect.w) - a;
                    int d = (int) (rect.y + rect.h) - b;
                    
                    g.setColor(DepthColor.getDepthColor(depth));
                    g.drawRect(a, b, c, d);
                    if (c > 100 && d > 50) {
                        g.drawString(content.get(i).getName(), a, b + 20);
                    }
                    g.setColor(Color.BLACK);
                    if (c - 3 > 0 && d - 3 > 0) {
                        content.get(i).draw(g, a + 2, b + 2, c - 3, d - 3);
                    }
                }
            }
        }*/
    }

    public List<SimpleFile> getItems() {
        return this.getContent();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setBounds(double x, double y, double w, double h) {
        bounds.setRect(x, y, w, h);
    }

    public boolean hasContent() {
        if (content != null && !content.isEmpty()) {
            hasContent = true;
        }
        return hasContent;
    }

    @Override
    public int compareTo(SimpleFile o) {
        Long size1 = this.getSize();
        Long size2 = o.getSize();
        
        return size2.compareTo(size1);
    }
}