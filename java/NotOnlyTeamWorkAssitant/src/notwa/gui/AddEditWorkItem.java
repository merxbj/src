package notwa.gui;
 
import java.awt.BorderLayout;
 
import javax.swing.JFrame;
import javax.swing.JLabel;
 
@SuppressWarnings("serial")
public class AddEditWorkItem extends JFrame {
    private String dialogType;
    public AddEditWorkItem(String dialogType) {
        this.dialogType = dialogType;
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - " + dialogType);
        this.setSize(750,300);
        this.setLocationRelativeTo(null);
         
        this.add(new JLabel("window for " + dialogType));
         
        this.setVisible(true);
    }
}
