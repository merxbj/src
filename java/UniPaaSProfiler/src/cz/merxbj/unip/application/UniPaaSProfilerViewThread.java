/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.unip.application;

import cz.merxbj.unip.gui.UniPaaSProfilerView;

/**
 *
 * @author tomas
 */
public class UniPaaSProfilerViewThread implements Runnable {
    private String logFilePath;

    public UniPaaSProfilerViewThread() {
    }
    
    public UniPaaSProfilerViewThread(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void run() {
        new UniPaaSProfilerView(logFilePath).setVisible(true);
    }
}
