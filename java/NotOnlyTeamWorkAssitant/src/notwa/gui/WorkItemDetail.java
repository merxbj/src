package notwa.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WorkItemDetail extends JPanel implements ActionListener {
    JButton hideDetail = new JButton("Hide detail");
    JTextArea description = new JTextArea();
    JTextField parent = new JTextField();
    JTextField deadline = new JTextField();
    JTextField lastModified = new JTextField();

    public WorkItemDetail() {
        this.setLayout(new GridLayout(0,2));
        
        this.add(new JLabel("Description"));
        description.setText(WorkItemTable.getSelected().getDescription());
        this.add(description);
        
        this.add(new JLabel("Parent WIT ID"));
        parent.setText(((Integer)WorkItemTable.getSelected().getParent().getId()).toString());
        this.add(parent);
        
        //this.add(new JLabel("Next action")); forgotten?
        
        this.add(new JLabel("Deadline"));
        deadline.setText(WorkItemTable.getSelected().getExpectedTimestamp().toString());
        this.add(deadline);
        
        //this.add(new JLabel("Added")); forgotten?

        
        this.add(new JLabel("Last update"));
        lastModified.setText(WorkItemTable.getSelected().getLastModifiedTimestamp().toString());
        this.add(lastModified);

        hideDetail.addActionListener(this);
        this.add(hideDetail);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            TabContent.hideDetail();
        }
    }
}
