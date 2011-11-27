/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.application;

import DA.TreeMap.TreeMapView;
import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;


/**
 *
 * @author mrneo
 */
public class MainTask extends Task {
    private File root;
    private TreeMapView canvasPanel;

    public MainTask(Application app) {
        super(app);
    }
    
    public void initializeTask(File root, TreeMapView canvasPanel) {
        this.root = root;
        this.canvasPanel = canvasPanel;
    }
    
    public synchronized void fireStop() {
        canvasPanel.fireStop();
        setMessage("Scanning stopped by user ...");
    }

    @Override
    protected Object doInBackground() throws Exception {
        canvasPanel.setTask(this);
        canvasPanel.start(root);

        return true;
    }

    public void setStatusMessage(String message) {
        this.setMessage(message);
    }
}
