package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import notwa.wom.Note;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class WorkItemDetail extends TabContent implements ActionListener {
    private static WorkItemDetail instance;
    JButton save = new JButton("Save");
    JButton showNotesHistory = new JButton("Show notes history");
    JTextArea description = new JTextArea();
    JTextArea latestNote = new JTextArea();
    JTextField parent = new JTextField();
    JTextField deadline = new JTextField();
    JTextField lastModified = new JTextField();
    JComboBox status,priority;
    JComboBox assignedUsers = new JComboBox();

    private WorkItemDetail() {
    }
    
    public static WorkItemDetail getInstance() {
        if (instance == null) {
            instance = new WorkItemDetail();
        }
        return instance;
    }
    
    public Component initComponents() {

        this.setLayout(new BorderLayout(5,5));
        JPanel descriptionPanel = new JPanel(new BorderLayout());

        descriptionPanel.add(new JLabel("Description"), BorderLayout.LINE_START);
        description.setBorder(BorderFactory.createEtchedBorder());
        descriptionPanel.add(description, BorderLayout.CENTER);
        
        JPanel boxesPanel = new JPanel(new GridLayout(0,2));
        // {
            boxesPanel.add(new JLabel("User"));
            boxesPanel.add(assignedUsers);
            
            boxesPanel.add(new JLabel("Priority"));
            boxesPanel.add(this.loadWorkItemPriorties());
            
            boxesPanel.add(new JLabel("State"));
            boxesPanel.add(this.loadWorkItemStates());
    
            boxesPanel.add(new JLabel("Parent WIT ID"));
            boxesPanel.add(parent);
                    
            boxesPanel.add(new JLabel("Deadline"));
            boxesPanel.add(deadline);
            
            boxesPanel.add(new JLabel("Last update"));
            boxesPanel.add(lastModified);
        // }
        JPanel topPanel = new JPanel(new BorderLayout(5,5));

        topPanel.add(descriptionPanel, BorderLayout.CENTER);
        topPanel.add(boxesPanel, BorderLayout.LINE_END);
            
        this.add(topPanel, BorderLayout.CENTER);
            
        JPanel notePanel = new JPanel(new BorderLayout());

        notePanel.add(new JLabel("Latest note"), BorderLayout.LINE_START);
        latestNote.setBorder(BorderFactory.createEtchedBorder());
        notePanel.add(latestNote, BorderLayout.CENTER);
            
        JPanel buttonsPanel = new JPanel();
        
        buttonsPanel.add(showNotesHistory);
        buttonsPanel.add(save);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5,5));

        bottomPanel.add(notePanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.LINE_END);
        
        this.add(bottomPanel, BorderLayout.PAGE_END);
        
        return this;
    }
    
    private JComboBox loadWorkItemStates() {
        status = new JComboBox();
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            status.addItem(WorkItemStatus.values()[s].name());
        }
        
        return status;
    }
    
    private JComboBox loadWorkItemPriorties() {
        priority = new JComboBox();
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priority.addItem(WorkItemPriority.values()[p]);
        }

        return priority;
    }
    
    public void updateDisplayedData() {
        try {
            assignedUsers.removeAllItems();
            UserCollection uc = WorkItemTable.getSelected().getProject().getAssignedUsers();
            for (User user : uc) {
                assignedUsers.addItem(user.getLogin());
            }
        } catch (Exception e) {};
        try {
            this.description.setText(WorkItemTable.getSelected().getDescription());
        } catch (Exception e) {};
        try {
            this.parent.setText(((Integer)WorkItemTable.getSelected().getParent().getId()).toString());
        } catch (Exception e) {};
        try {
            this.deadline.setText(WorkItemTable.getSelected().getExpectedTimestamp().toString());
        } catch (Exception e) {};
        try {
            this.lastModified.setText(WorkItemTable.getSelected().getLastModifiedTimestamp().toString());
        } catch (Exception e) {};
        try {
            assignedUsers.setSelectedItem(WorkItemTable.getSelected().getAssignedUser().getLogin());
        } catch (Exception e){};
        try {
            priority.setSelectedItem(WorkItemTable.getSelected().getPriority());
        } catch (Exception e){};
        try {
            status.setSelectedItem(WorkItemTable.getSelected().getStatus().name());
        } catch (Exception e){};
        try {
            Note note = WorkItemTable.getSelected().getNoteCollection().getLatestNote();
            this.latestNote.setText(String.format("%s : %s",note.getAuthor().getLogin(), note.getNoteText()));
        } catch (Exception e) { this.latestNote.setText(null); };
    }
}
