package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JStatusBar extends JPanel {
    private static JStatusBar singleton;
    JProgressBar mainProgressBar = new JProgressBar();
    JLabel statusBarText = new JLabel();
    
    public static JStatusBar getInstance() {
        if (singleton == null) {
            singleton = new JStatusBar();
        }
        return singleton;
    }
    
    private JStatusBar() {
        // for testing
        setStatusBarText("Synchronizing with repository ...");
        setProgressBarValue(5);
        //
        
        this.setLayout(new BorderLayout());
        this.add(statusBarText, BorderLayout.LINE_START);
        mainProgressBar.setPreferredSize(new Dimension(300,20));
        mainProgressBar.setStringPainted(true);
        this.add(mainProgressBar, BorderLayout.LINE_END);
    }
    
    public void setStatusBarText(String text) {
        this.statusBarText.setText(text);
        this.update(this.getGraphics());
    }
    
    public void setProgressBarValue(int v) {
        mainProgressBar.setValue(v);
        mainProgressBar.updateUI();
    }
    
    public void setProgressBarMaximum(int v) {
        mainProgressBar.setMaximum(v);
        mainProgressBar.updateUI();
    }
}
