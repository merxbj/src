/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.TreeMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mrneo
 */
public class SimpleFile implements Iterable<SimpleFile> {

    protected String name;                    //file/folder name
    protected String path;                    //full file/folder path on disk
    protected SimpleFile parent = null;
    protected List<SimpleFile> content;     //list containing complete content of directory
    protected SmallFileAggregator aggregator;
    protected long size = 0;                  //complete file / directory size 
    private Rect rectangle;                 //rectangle representing file size in hierarchy
    protected int depth;
    protected boolean isFile = false;
    private boolean isMouseOver = false;
    private boolean isSelected = false;
    protected boolean aggregated = false;

    public SimpleFile() {
    }
    
    public SimpleFile(File file) {
        this(file, 0);
    }

    public SimpleFile(File file, int depth) {
        name = file.getName();
        path = file.getAbsolutePath();
        this.depth = depth;
        size = file.length();
        isFile = file.isFile();
        
        if (file.isDirectory()) {
            content = new ArrayList<SimpleFile>();
            aggregator = new SmallFileAggregator(this);
            this.content.add(aggregator);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setParent(SimpleFile folder) {
        this.parent = folder;
    }

    public void add(SimpleFile sf) {
        add(sf, 0);
    }
    
    public void add(SimpleFile sf, long filePaintTreshold) {
        if (sf.getSize() < filePaintTreshold) {
            aggregator.aggregate(sf);
        } else {
            content.add(sf);
        }
        
        updateSize(sf.getSize());
    }
    
    public void forceAggregation(long fileSizeTreshold) {
        if (content != null) {
            List<SimpleFile> aggregatedFiles = new LinkedList<SimpleFile>();
            for (SimpleFile sf : content) {
                sf.forceAggregation(fileSizeTreshold);
                if (sf.getSize() < fileSizeTreshold && sf.isValid()) {
                    aggregatedFiles.add(sf);
                    aggregator.aggregate(sf);
                }
            }
            this.content.removeAll(aggregatedFiles);
        }
    }

    public void relaxAggregation(long fileSizeTreshold) {
        if (aggregator != null) {
            content.addAll(aggregator.relax(fileSizeTreshold));
        }
        if (content != null) {
            for (SimpleFile sf : content) {
                sf.relaxAggregation(fileSizeTreshold);
            }
        }
    }

    public long getSize() {
        return size;
    }
    
    public void updateSize(long size) {
        this.size += size;
        if (parent != null) {
            parent.updateSize(size);
        }
    }

    public String getSizeForPaint() {
        long sizeInB = this.getSize();
        if (sizeInB <= 1024) {
            return sizeInB + "b";
        }

        long sizeInKb = sizeInB / 1024;
        if (sizeInKb <= 1024) {
            return sizeInKb + "Kb";
        }

        long sizeInMb = sizeInKb / 1024;
        if (sizeInMb <= 1024) {
            return sizeInMb + "Mb";
        }

        return sizeInMb / 1024 + "Gb";
    }

    public List<SimpleFile> getContent() {
        return content;
    }

    public void removeAllContent() {
        if (content != null) {
            content.clear();
        }
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
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        if (isMouseOver) {
            g.setColor(Color.WHITE);
        } else if (isSelected) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(getDepthColor(depth, isFile));
        }
        g.fillRect(x + 1, y + 1, width - 1, height - 1);

        g.setColor(Color.BLACK);
        String title = name + " - " + getSizeForPaint();
        TreeMapViewStatics.drawString(g, title, x, y + 10, width, height);

        if (depth + 1 <= TreeMapView.CURRENT_DEPTH) {
            drawContent(g, x, y, width, height);
        }
    }

    protected void drawContent(Graphics2D g, int x, int y, int width, int height) {
        int contentMaxHeight = height - TreeMapViewStatics.DEFAULT_CONTENT_OFFSET;
        int contentY = y + TreeMapViewStatics.DEFAULT_CONTENT_OFFSET;
        if (this.hasContent() && (contentMaxHeight > 0)) {
            SquarifiedLayout.layout(content, new Rect(x, contentY, width, contentMaxHeight));

            for (int i = 0; i < content.size(); i++) {
                Rect rect = content.get(i).getBounds();
                if (rect != null) {
                    int a = (int) rect.x;
                    int b = (int) rect.y;
                    int c = (int) (rect.x + rect.width) - a;
                    int d = (int) (rect.y + rect.height) - b;

                    if (c - 6 > 0 && d - 6 > 0) {
                        content.get(i).draw(g, a + 3, b + 3, c - 6, d - 6);
                    }
                }
            }
        }
    }
    
    public boolean hasContent() {
        if (content != null && !content.isEmpty()) {
            return true;
        }
        return false;
    }

    public Rect getBounds() {
        return rectangle;
    }

    public void setBounds(Rect bounds) {
        this.rectangle = bounds;
    }

    public SimpleFile contains(int x, int y) {
        if (rectangle != null) {
            if (rectangle.contains(x, y)) {
                if (hasContent() && (depth + 1 <= TreeMapView.CURRENT_DEPTH)) {
                    SimpleFile sf = null;
                    for (int i = 0; i < content.size(); i++) {
                        sf = content.get(i).contains(x, y);

                        if (sf != null) {
                            return sf;
                        }
                    }
                }
                return this;
            }
        }
        return null;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setMouseOver(boolean mouseOver) {
        isMouseOver = mouseOver;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleFile other = (SimpleFile) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    public boolean isAggregated() {
        return aggregated;
    }
    
    public Color getDepthColor(int depth, boolean isFile) {
        int currentDepth = (depth == 0) ? 1 : depth + 1;
        if (!isFile) {
            int red = ((100 - (10 * currentDepth)) > 0) ? 100 - (10 * currentDepth) : 0;
            int green = ((225 - (10 * currentDepth)) > 0) ? 225 - (10 * currentDepth) : 0;
            int blue = 255 - (10 * currentDepth);

            return new Color(red, green, blue);
        } else {
            int red = ((100 - (10 * currentDepth)) > 0) ? 100 - (10 * currentDepth) : 0;
            int green = 255 - (10 * currentDepth);
            int blue = ((150 - (10 * currentDepth)) > 0) ? 150 - (10 * currentDepth) : 0;

            return new Color(red, green, blue);
        }
    }
    
    public boolean isValid() {
        return true;
    }

}
