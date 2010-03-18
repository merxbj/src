package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SettingsDialog extends JFrame {

    public SettingsDialog() {
    }
    
    public void initSettingsDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Settings");
        this.setSize(750,300);
        this.setLocationRelativeTo(null);
        
        this.add(new JLabel("window for settings"));
        
        this.setVisible(true);
    }
}
