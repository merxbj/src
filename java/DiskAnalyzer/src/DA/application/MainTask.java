/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.application;

import DA.DiskAnalyzerView;
import DA.common.SimpleFile;
import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;


/**
 *
 * @author mrneo
 */
public class MainTask extends Task {
    private File root;
    private boolean stop = false;
    private DiskAnalyzerView view;

    public MainTask(Application app) {
        super(app);
    }
    
    public void setRoot(File root) {
        this.root = root;
    }
    
    public void registerView(DiskAnalyzerView dav) {
        this.view = dav;
    }
    
    public synchronized void fireStop() {
        stop = true;
        setMessage("Scanning stopped by user ...");
    }

    @Override
    protected Object doInBackground() throws Exception {
        TreeView tv = new TreeView();
        view.setComponent(tv);
        tv.start(root);
        
        return null;
    }
}
