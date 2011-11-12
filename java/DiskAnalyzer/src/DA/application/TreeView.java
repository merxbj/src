/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.application;

import DA.common.SimpleFile;
import DA.common.TMAlgorithm;
import DA.common.TMAlgorithmSquarified;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author mrneo
 */
public class TreeView extends JPanel implements Observer {
    private SimpleFile rootDir;
    private TMAlgorithm algorithm;
    private File root;
    private Image offscreen;
    private Graphics2D backBuffer;
    
    public TreeView() {
        super();
    }
    
    public void start(File root) {
        this.root = root;
        rootDir = new SimpleFile(root);
        algorithm = new TMAlgorithmSquarified();
        algorithm.addObserver(this);
        algorithm.initialize(rootDir, this);
        
        startAnalyze();
    }
    
    public void startAnalyze() {
        analyzeFiles(root.listFiles(), rootDir, 0);
    }
    
    @Override
    public void paint(Graphics g) {
        int width = this.getSize().width;
        int height = this.getSize().height;
        offscreen = createImage(width, height);
        backBuffer = (Graphics2D) offscreen.getGraphics();
        backBuffer.setColor(Color.WHITE);
        backBuffer.fillRect(0, 0, width, height);
        backBuffer.setColor(Color.BLACK);

        if (rootDir == null) {
            return;
        }

        rootDir.setBounds(new Rectangle(0, 0, getWidth(), getHeight()));

        algorithm.draw(backBuffer, rootDir);
        g.drawImage(offscreen, 0, 0, this);
    }

    private void analyzeFiles(File[] rootList, SimpleFile dirContent, int depth) {

        depth++;
        for (File file : rootList) {
            //mainTask.setStatusMessage(file.getAbsolutePath());
            if (file.canRead() && !isSymlink(file)) {
                if (file.isDirectory()) {
                    File[] content = file.listFiles();
                    if (content.length > 0) {
                        SimpleFile subDir = new SimpleFile(file);
                        analyzeFiles(content, subDir, depth);
                        dirContent.add(subDir);
                        dirContent.setDepth(depth);
                    }
                } else if (file.isFile() && file.length() > 0) {
                    dirContent.add(new SimpleFile(file));
                }
            }
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
        }
        catch (Exception e) {
            return true;
        }
    }
    
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
