package notwa.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;
 
public class WorkItemEditor extends JFrame implements ActionListener {
    private JComboBox existingProjects,priorities,states;
    private JTextField newProjectName = new JTextField();
    private JTextField subject = new JTextField();
    private JButton okButton, stornoButton, chooseColorButton;
    
    public WorkItemEditor() {
    }
    
    public void initAddDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Add");
        this.initDialog();
    }
    
    public void initEditDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Edit");
        this.initDialog();
    }
    
    private void initDialog() {
        this.setLayout(new GridLayout(2,0));
        this.setSize(750,300);

        JPanel jp = new JPanel(new GridLayout(0,2));

        jp.add(new JLabel("Attach to existing project"));
        jp.add(this.loadExistingProjects());
        
        jp.add(new JLabel("Create new Project"));
        jp.add(newProjectName);
        
        jp.add(new JLabel("Choose project color"));
        chooseColorButton = new JButton("Browse");
        chooseColorButton.addActionListener(this);
        jp.add(chooseColorButton);
        
        jp.add(new JLabel("Subject"));
        jp.add(subject);
        
        jp.add(new JLabel("Priority"));
        jp.add(this.loadWorkItemPriorties());
        
        jp.add(new JLabel("State"));
        jp.add(this.loadWorkItemStates());
        
        this.add(jp);
        this.add(this.initButtons());
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private JPanel initButtons() {
        JPanel jp = new JPanel();
        
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        jp.add(okButton);
        jp.add(stornoButton);
        
        return jp;
    }
    
    private JComboBox loadExistingProjects() {
        existingProjects = new JComboBox();
        ProjectCollection pc = new ProjectCollection();
        for (Project p : pc) {
            existingProjects.addItem(new JComboBoxItemCreator(p, p.getName()));
        }
        
        return existingProjects;
    }
    
    private JComboBox loadWorkItemStates() {
        states = new JComboBox();
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            states.addItem(new JComboBoxItemCreator(WorkItemStatus.values()[s].getValue(),
                                                    WorkItemStatus.values()[s].name()));
        }
        
        return states;
    }
    
    private JComboBox loadWorkItemPriorties() {
        priorities = new JComboBox();
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priorities.addItem(new JComboBoxItemCreator(WorkItemPriority.values()[p].getValue(),
                                                        WorkItemPriority.values()[p].name()));
        }

        return priorities;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            this.setVisible(false);
        }
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
        if(ae.getSource() == chooseColorButton) { //TODO all colors will be used this way
            JColorChooser colorChooser = new JColorChooser();
            JDialog jd = JColorChooser.createDialog( chooseColorButton,
                                        "Project color chooser",
                                        true,
                                        colorChooser,
                                        this,
                                        null);
            jd.setVisible(true); //TODO not done yet 
        }
    }
}
