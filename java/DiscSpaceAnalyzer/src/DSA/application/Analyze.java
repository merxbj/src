/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.application;

import DSA.common.SimpleDir;
import DSA.common.SimpleFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author mrneo
 */
public class Analyze extends Task {
    private File pathToAnalyze;
    private boolean stop = false;
    private SimpleDir structure;

    public Analyze(Application app) {
        super(app);
    }

    public Analyze(Application app, SimpleDir structure) {
        super(app);
        this.structure = structure;
    }

    public void setPathToAnalyze(File root) {
        this.pathToAnalyze = root;
    }
    
    private void startAnalyze() {
        File[] rootList = pathToAnalyze.listFiles();
        this.analyzeFiles(rootList, structure);
    }

    private void analyzeFiles(File[] rootList, SimpleDir content) {
        if (!stop) {
            for (File file : rootList) {
                setMessage(file.getAbsolutePath());
                //System.out.println(file.getAbsolutePath() + " " + file.length());
                if (file.isDirectory() && file.canRead()) {
                    SimpleDir folderStructure = newSimpleDir(file);
                    analyzeFiles(file.listFiles(), folderStructure);
                    content.add(folderStructure);
                }
                else if(file.isFile()) {
                    content.add(this.newSimpleFile(file));
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
