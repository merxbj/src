package notwa.gui;
import java.awt.BorderLayout;

import javax.swing.JDialog;

public class FilteringDialog extends JDialog {

    public FilteringDialog() {
        
    }
    
    public void initFilteringDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Configure Sorting / Filtering");
        this.setSize(500,200);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
                
       
        this.setVisible(true);
    }
}
