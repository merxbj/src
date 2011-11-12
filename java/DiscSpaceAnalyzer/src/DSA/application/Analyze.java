/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.application;

import DSA.common.SimpleDir;
import DSA.common.SimpleFile;
import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author mrneo
 */
public class Analyze extends Task {
    private File root;
    private boolean stop = false;
    private SimpleDir dirStructure;

    public Analyze(Application app) {
        super(app);
    }

    public Analyze(Application app, SimpleDir dirStructure) {
        super(app);
        this.dirStructure = dirStructure;
    }

    public void setPathToAnalyze(File root) {
        this.root = root;
    }
    
    private void startAnalyze() {
        File[] rootList = root.listFiles();
        this.analyzeFiles(rootList, dirStructure);
    }

    private void analyzeFiles(File[] rootList, SimpleDir dirContent) {
        if (!stop) {
            for (File file : rootList) {
                setMessage(file.getAbsolutePath());
                if (file.isDirectory() && file.canRead()) {
                    SimpleDir subDirStructure = newSimpleDir(file);
                    analyzeFiles(file.listFiles(), subDirStructure);
                    dirContent.add(subDirStructure);
                }
                else if(file.isFile()) {
                    dirContent.add(this.newSimpleFile(file));
                }
            }
        }
    }

    private SimpleDir newSimpleDir(File file) {
        SimpleDir sd = new SimpleDir();
        sd.setName(file.getAbsolutePath());
        sd.setDirectorySize(file.length());
        
        return sd;
    }
    
    private SimpleFile newSimpleFile(File file) {
        SimpleFile sf = new SimpleFile();
        sf.setName(file.getAbsolutePath());
        sf.setSize(file.length());
        
        return sf;
    }
    
    public synchronized void fireStop() {
        stop = true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        while(!stop) {
            this.startAnalyze();
            setMessage("Scanning completed ...");
            stop = true;
        }
        return null;
    }
}
