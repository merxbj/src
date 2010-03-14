package notwa.gui;
 
import java.awt.BorderLayout;
 
import javax.swing.JFrame;
import javax.swing.JLabel;
 
@SuppressWarnings("serial")
public class WorkItemEditor extends JFrame {
    public WorkItemEditor() {
    }
    
    public void initAddDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Add");
        this.initDialog();
        this.add(new JLabel("window for Add"));
    }
    
    public void initEditDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Edit");
        this.initDialog();
        this.add(new JLabel("window for Edit"));
    }
    
    private void initDialog() {
        this.setLayout(new BorderLayout());
        this.setSize(750,300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
