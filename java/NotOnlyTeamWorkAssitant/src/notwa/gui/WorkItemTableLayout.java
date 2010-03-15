package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class WorkItemTableLayout extends JPanel {
    
    public WorkItemTableLayout(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        
        JPanel witTable = new WorkItemTable(wic);
        this.add(witTable, BorderLayout.CENTER);
    }
}
