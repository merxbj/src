package notwa.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WorkItemDetail extends JPanel implements ActionListener {
    private static WorkItemDetail singleton;
    JButton hideDetail = new JButton("Hide detail");
    JTextArea description = new JTextArea();
    JTextField parent = new JTextField();
    JTextField deadline = new JTextField();
    JTextField lastModified = new JTextField();

    private WorkItemDetail() {
    }
    
    public static WorkItemDetail getInstance() {
        if (singleton == null) {
            singleton = new WorkItemDetail();
        }
        return singleton;
    }
    
    public Component initComponents() {
        this.setLayout(new GridLayout(0,2));

        this.add(new JLabel("Description"));
        this.add(description);
        
        this.add(new JLabel("Parent WIT ID"));
        this.add(parent);
        
        //this.add(new JLabel("Next action")); forgotten?
        
        this.add(new JLabel("Deadline"));
        this.add(deadline);
        
        //this.add(new JLabel("Added")); forgotten?

        this.add(new JLabel("Last update"));
        this.add(lastModified);

        hideDetail.addActionListener(this);
        this.add(hideDetail);
        
        return this;
    }
    
    public void fillWithActualData() {
        try {
            this.description.setText(WorkItemTable.getSelected().getDescription());
        } catch (Exception e) {
            //we dont care, it is possible that something is not set
        }
        try {
            this.parent.setText(((Integer)WorkItemTable.getSelected().getParent().getId()).toString());
        } catch (Exception e) {};
        try {
            this.deadline.setText(WorkItemTable.getSelected().getExpectedTimestamp().toString());
        } catch (Exception e) {};
        try {
            this.lastModified.setText(WorkItemTable.getSelected().getLastModifiedTimestamp().toString());
        } catch (Exception e) {};
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            TabContent.hideDetail();
        }
    }
}
