package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class JStatusBar extends JPanel {
    JProgressBar jpb = new JProgressBar();
    JLabel statusBarText = new JLabel();
    public JStatusBar() {
        
        // for testing
        setStatusBarText("Synchronizing with repository ...");
        setProgressBarValue(5);
        
        
        this.setLayout(new BorderLayout());
        this.add(statusBarText, BorderLayout.LINE_START);
        jpb.setPreferredSize(new Dimension(300,20));
        jpb.setStringPainted(true);
        this.add(jpb, BorderLayout.LINE_END);
    }
    
    public void setStatusBarText(String text) {
        this.statusBarText.setText(text);
        this.update(this.getGraphics());
    }
    
    public void setProgressBarValue(int v) {
        jpb.setValue(v);
        jpb.updateUI();
    }
    
    public void setProgressBarMaximum(int v) {
        jpb.setMaximum(v);
        jpb.updateUI();
    }
}
