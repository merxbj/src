package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class SettingsDialog extends JDialog {

    public SettingsDialog() {
    }
    
    public void initSettingsDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Settings");
        this.setSize(750,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(new JLabel("window for settings"));
        
        this.setVisible(true);
    }
}
