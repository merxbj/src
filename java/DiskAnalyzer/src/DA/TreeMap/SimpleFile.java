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
import java.util.List;

/**
 *
 * @author mrneo
 */
public class SimpleFile implements Iterable<SimpleFile> {

    private String name;                    //file/folder name
    private String path;                    //full file/folder path on disk
    private SimpleFile parent = null;
    private List<SimpleFile> content;       //list containing complete content of directory
    private long size = 0;                  //complete file / directory size 
    private long folderSize = 0;            //directory size (usualy something like 4096b)
    private boolean isSizeCalculated;
    private Rect rectangle;                 //rectangle representing file size in hierarchy
    private int depth;
    private boolean isFile = false;
    private boolean isMouseOver = false;
    private boolean isSelected = false;

    public SimpleFile(File file) {
        this(file, 0);
    }

    public SimpleFile(File file, int depth) {
        name = file.getName();
        path = file.getAbsolutePath();
        this.depth = depth;
        folderSize = file.length();
        isSizeCalculated = (isFile) ? true : false;
        isFile = file.isFile();
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

    public void add(SimpleFile so) {
        if (content == null) {
            content = new ArrayList<SimpleFile>();
            //content = Collections.synchronizedList(content);
        }
        resetMeAndMyParetsSize();
        content.add(so);
    }

    public long getSize() {
        if (!isSizeCalculated) {
            if (hasContent()) {
                for (int i = 0; i < content.size(); i++) {
                    size += content.get(i).getSize();
                }
            }
            this.isSizeCalculated = true;
        }
        return folderSize + size;
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

    public void resetMeAndMyParetsSize() {
        size = 0;
        isSizeCalculated = false;

        if (parent != null) {
            parent.resetMeAndMyParetsSize();
        }
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
            g.setColor(TreeMapViewStatics.getDepthColor(depth, isFile));
        }
        g.fillRect(x + 1, y + 1, width - 1, height - 1);

        g.setColor(Color.BLACK);
        String title = name + " - " + getSizeForPaint();
        TreeMapViewStatics.drawString(g, title, x, y + 10, width, height);

        if (depth + 1 <= TreeMapView.CURRENT_DEPTH) {
            drawContent(g, x, y, width, height);
        }
    }

    private void drawContent(Graphics2D g, int x, int y, int width, int height) {
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
}
