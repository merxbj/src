package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import notwa.dal.UserDal;
import notwa.wom.ContextManager;
import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class WorkItemDetail extends TabContent implements ActionListener {
    private static WorkItemDetail singleton;
    JButton hideDetail = new JButton("Hide detail");
    JButton save = new JButton("Save");
    JButton showNotesHistory = new JButton("Show notes history");
    JTextArea description = new JTextArea();
    JTextArea latestNote = new JTextArea();
    JTextField parent = new JTextField();
    JTextField deadline = new JTextField();
    JTextField lastModified = new JTextField();
    JComboBox status,priority,assignedUsers;

    private WorkItemDetail() {
    }
    
    public static WorkItemDetail getInstance() {
        if (singleton == null) {
            singleton = new WorkItemDetail();
        }
        return singleton;
    }
    
    public Component initComponents() {

        this.setLayout(new BorderLayout(5,5));
        JPanel descriptionPanel = new JPanel(new BorderLayout());

        descriptionPanel.add(new JLabel("Description"), BorderLayout.LINE_START);
        description.setBorder(BorderFactory.createEtchedBorder());
        descriptionPanel.add(description, BorderLayout.CENTER);
        
        JPanel boxesPanel = new JPanel(new GridLayout(0,2));
        // {
            boxesPanel.add(new JLabel("")); // separator
            
            boxesPanel.add(hideDetail);
            hideDetail.addActionListener(this);
            
            boxesPanel.add(new JLabel("User"));
            boxesPanel.add(this.loadProjectUsers());
            
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
    
    private JComboBox loadProjectUsers() {
        assignedUsers = new JComboBox();
        
        UserCollection uc = new UserCollection();
        uc.setCurrentContext(ContextManager.getInstance().newContext());
        UserDal ud = new UserDal(super.getCurrentConnectionInfo(), uc.getCurrentContext());
        ud.Fill(uc);
        
        for (User user : uc) {
            assignedUsers.addItem(user.getLogin());
        }

        return assignedUsers;
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
        try {
            assignedUsers.setSelectedItem(WorkItemTable.getSelected().getAssignedUser().getLogin());
        } catch (Exception e){};
        try {
            priority.setSelectedItem(WorkItemTable.getSelected().getPriority());
        } catch (Exception e){};
        try {
            status.setSelectedItem(WorkItemTable.getSelected().getStatus().name());
        } catch (Exception e){};
        try { // show something / TODO : add to WorkItem> getLatestNote()
            NoteCollection nc = WorkItemTable.getSelected().getNoteCollection();
            Note note = nc.get(nc.size()-1);
            this.latestNote.setText(note.getNoteText());
        } catch (Exception e) {};
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            TabContent.hideDetail();
        }
    }
}
