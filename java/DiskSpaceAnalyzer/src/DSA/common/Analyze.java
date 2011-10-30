/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.common;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author mrneo
 */
public class Analyze extends Task {
    private File pathToAnalyze;
    private boolean stop = false;

    public Analyze(Application app) {
        super(app);
    }

    public void setPathToAnalyze(File root) {
        this.pathToAnalyze = root;
    }
    
    private void startAnalyze() {
        File[] rootList = pathToAnalyze.listFiles();
        this.analyzeFiles(pathToAnalyze, rootList);
    }

    private void analyzeFiles(File parent, File[] rootList) {
        if (!stop) {
            for (File file : rootList) {
                setMessage(file.getAbsolutePath());
                //System.out.println(file.getAbsolutePath() + " " + file.length());
                if (file.isDirectory() && file.canRead()) {
                     analyzeFiles(file, file.listFiles());
                }
            }
        }
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
