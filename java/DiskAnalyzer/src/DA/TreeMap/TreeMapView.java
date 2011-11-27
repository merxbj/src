/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.TreeMap;

import DA.application.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;

/**
 *
 * @author mrneo
 */
public class TreeMapView extends JComponent implements MouseListener, MouseMotionListener {

    public static int CURRENT_DEPTH = 0;
    public static int MAX_DEPTH = 0;

    private TreeMapViewHistory history = TreeMapViewHistory.getInstance();
    private MainTask task;
    private Insets insets;
    private SimpleFile rootDir;
    private SimpleFile selectedRoot;
    private SimpleFile selected;
    private SimpleFile hightlighted;
    private File root;
    private boolean stop = false;

    public TreeMapView() {
        super();
        init();
    }

    public void setTask(MainTask task) {
        this.task = task;
    }

    private void init() {
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void start(File root) {
        this.root = root;
        rootDir = new SimpleFile(root);
        selectedRoot = rootDir;
        history.addToHistory(rootDir);
        startAnalyze();
    }

    public void startAnalyze() {
        analyzeFiles(root.listFiles(), rootDir, 0);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (insets == null) {
            insets = getInsets();
        }

        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        g.setFont(TreeMapViewStatics.DEFAULT_VIEW_FONT);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        if (selectedRoot != null) {
            selectedRoot.setBounds(new Rect(x, y, width, height));
            selectedRoot.draw((Graphics2D) g, x, y, width, height);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        String toolTipText = "";

        if (selectedRoot != null) {
            SimpleFile file = selectedRoot.contains(x, y);
            if (file != null) {
                toolTipText = "<html>"
                        + "<b>Name</b> : " + file.getName() + "<br />"
                        + "<b>Path</b> : " + file.getPath() + "<br />"
                        + "<b>Size</b> : " + file.getSizeForPaint()
                        + "</html>";
            }
        }

        return toolTipText;
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        Point pt = new Point(event.getX(), event.getY());
        return pt;
    }

    private void analyzeFiles(File[] rootList, SimpleFile parent, int depth) {
        if (!stop) {
            depth++;
            for (File file : rootList) {
                if (file.canRead() && !isSymlink(file)) {
                    if (file.isDirectory()) {
                        task.setStatusMessage("Scanning : " + file.getAbsolutePath());

                        File[] content = file.listFiles();
                        if (content != null) {
                            if (content.length > 0) {
                                SimpleFile subDir = new SimpleFile(file, depth);
                                subDir.setParent(parent);
                                parent.add(subDir);

                                analyzeFiles(content, subDir, depth);
                            }
                        }
                    } else if (file.isFile() && file.length() > 0) {
                        SimpleFile contentFile = new SimpleFile(file, depth);
                        contentFile.setParent(parent);
                        parent.add(contentFile);
                        this.repaint();
                    }
                }
            }
            updateDepthGlobals(depth);
        }
    }

    private void updateDepthGlobals(int depth) {
        if (depth > MAX_DEPTH) {
            MAX_DEPTH = depth;
        }
        if (depth <= 5 && depth > CURRENT_DEPTH) {
            CURRENT_DEPTH = depth;
        }
    }

    public boolean isSymlink(File file) {
        try {
            File canon;
            if (file.getParent() == null) {
                canon = file;
            } else {
                File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
            }
            return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
        } catch (Exception e) {
            return true;
        }
    }

    public synchronized void fireStop() {
        this.stop = true;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            SimpleFile file = selectedRoot.contains(e.getX(), e.getY());
            if (file != null) {
                selectedRoot = file;
                history.addToHistory(file);
                this.repaint();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            //TODO: right click
        } else {
            SimpleFile file = selectedRoot.contains(e.getX(), e.getY());
            if (file != null) {
                if (selected != null) {
                    selected.setSelected(false);
                }
                selected = file;
                selected.setSelected(true);
                this.repaint();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        if (selectedRoot != null) {
            SimpleFile file = selectedRoot.contains(e.getX(), e.getY());
            if (file != null) {
                if (hightlighted != null) {
                    hightlighted.setMouseOver(false);
                }
                hightlighted = file;
                hightlighted.setMouseOver(true);
                this.repaint();
            }
        }
    }
}
