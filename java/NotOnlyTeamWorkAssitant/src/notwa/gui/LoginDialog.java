package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class LoginDialog extends JFrame {
    public LoginDialog() {
    }
    
    public void initLoginDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Sign-in");
        this.setSize(500,200);
        this.setLocationRelativeTo(null);
        
        this.add(new JLabel("window for sign-in"));
        
        this.setVisible(true);
    }
}
