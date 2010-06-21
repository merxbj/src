/*
 * WorkItemDetail
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import notwa.dal.NoteDal;
import notwa.dal.WorkItemDal;
import notwa.gui.components.KeyValueComboBox;
import notwa.logger.LoggingFacade;
import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.wom.Project;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class WorkItemDetail extends WorkItemDetailLayout implements ActionListener {
    private JButton save,addNote;
    private JTextArea description;
    private JTextArea latestNote;
    private JTextField parent;
    private JFormattedTextField deadline;
    private JTextField lastModified;
    private KeyValueComboBox<WorkItemStatus> statuses;
    private KeyValueComboBox<WorkItemPriority> priorities;
    private KeyValueComboBox<User> assignedUsers;
    private WorkItem currentWorkItem;
    private WorkItemNoteHistoryTable winht;
    private TabContent tc;

    public WorkItemDetail() {
        init();
    }
    
    @Override
    public void init() {

        this.save = new JButton("Save");
        this.addNote = new JButton("Add note");
        this.description = new JTextArea();
        this.latestNote = new JTextArea();
        this.parent = new JTextField();
        //this.deadline = new JFormattedTextField();
        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter("##.##.#### ##:##");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        this.deadline = new JFormattedTextField(mf);
        this.lastModified = new JTextField();
        this.statuses = new KeyValueComboBox<WorkItemStatus>();
        this.priorities = new KeyValueComboBox<WorkItemPriority>();
        this.assignedUsers = new KeyValueComboBox<User>();
        
        this.setLayout(new BorderLayout(5,5));
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        
        JPanel pDescription = new JPanel();
        JLabel lDescription = new JLabel("Description");
        pDescription.add(lDescription);
        descriptionPanel.add(pDescription, BorderLayout.LINE_START);
        description.setLineWrap(true);
        descriptionPanel.add(new JScrollPane(description), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(5,5));

        topPanel.add(descriptionPanel, BorderLayout.CENTER);
        topPanel.add(this.initBoxes(), BorderLayout.LINE_END);
            
        this.add(topPanel, BorderLayout.CENTER);
            
        JPanel notePanel = new JPanel(new BorderLayout());

        JPanel pLatestNote = new JPanel();
        JLabel lLatestNote = new JLabel("Latest note");
        pLatestNote.add(lLatestNote);
        notePanel.add(pLatestNote, BorderLayout.LINE_START);
        latestNote.setLineWrap(true);
        notePanel.add(new JScrollPane(latestNote), BorderLayout.CENTER);
            
        JPanel buttonsPanel = new JPanel();
        
        save.addActionListener(this);
        addNote.addActionListener(this);
        
        buttonsPanel.add(addNote);
        buttonsPanel.add(save);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(notePanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.LINE_END);
        bottomPanel.setPreferredSize(new Dimension(0,35));
        
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }
    
    private JPanel initBoxes() {
        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(null);
        boxesPanel.setPreferredSize(new Dimension(401, 80));
        boxesPanel.setMinimumSize(new Dimension(0,80));

        JLabel lUser = new JLabel("User");
        boxesPanel.add(lUser);
        lUser.setBounds(5, 8, 45, 15);
        boxesPanel.add(assignedUsers);
        assignedUsers.setBounds(60, 4, 120, 22);
            
        JLabel lPriority = new JLabel("Priority");
        boxesPanel.add(lPriority);
        lPriority.setBounds(5, 33, 45, 15);
        this.loadWorkItemPriorties();
        boxesPanel.add(priorities);
        priorities.setBounds(60, 29, 120, 22);

        JLabel lStatus = new JLabel("Status");
        boxesPanel.add(lStatus);
        lStatus.setBounds(5, 59, 45, 15);
        this.loadWorkItemStatuses();
        boxesPanel.add(statuses);
        statuses.setBounds(60, 55, 120, 22);

        JLabel lParent = new JLabel("Parent WIT ID");
        boxesPanel.add(lParent);
        lParent.setBounds(185, 9, 86, 15);
        boxesPanel.add(parent);
        parent.setBounds(271, 6, 125, 22);

        JLabel lDeadline = new JLabel("Deadline");
        boxesPanel.add(lDeadline);
        lDeadline.setBounds(185, 33, 86, 15);
        boxesPanel.add(deadline);
        deadline.setBounds(271, 30, 125, 22);
            
        JLabel lLastUpdate = new JLabel("Last update");
        boxesPanel.add(lLastUpdate);
        lLastUpdate.setBounds(185, 59, 86, 15);
        boxesPanel.add(lastModified);
        lastModified.setBounds(271, 54, 125, 22);
        lastModified.setEditable(false);
        
        return boxesPanel;
    }
    
    private void loadWorkItemStatuses() {
        statuses.removeAllItems();
        for (WorkItemStatus wis : WorkItemStatus.values()) {
            statuses.addItem(wis, wis.toString());
        }
    }
    
    private void loadWorkItemPriorties() {
        priorities.removeAllItems();
        for (WorkItemPriority wip : WorkItemPriority.values()) {
            priorities.addItem(wip, wip.toString());
        }
    }
        
    private void loadAssignedUsers(UserCollection uc) {
        assignedUsers.removeAllItems();
        if (uc != null) {
            for (User user : uc) {
                assignedUsers.addItem(user, user.getLogin());
            }
        } else {
            this.assignedUsers.addItem(new User(0), "Unknown");
        }
    }
    
    public void setDescription(String description) {
        this.description.setText(description);
    }
    
    public void setParent(int id) {
        this.parent.setText(((Integer)id).toString());
    }
    
    public void setDeadline(Date deadline) {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            this.deadline.setText(formatter.format(deadline));
        } catch (Exception e) { 
            this.deadline.setText("00.00.0000 00:00");
        }
    }
    
    public void setLastModified(Date lastModified) {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            this.lastModified.setText(formatter.format(lastModified));
        } catch (Exception e) {
            this.lastModified.setText("00.00.0000 00:00");
        }
    }
    
    public void setPriority(WorkItemPriority wip) {
        this.priorities.setSelectedKey(wip);
    }
    
    public void setStatus(WorkItemStatus wis) {
        this.statuses.setSelectedKey(wis);
    }
    
    public void setLastNote(Note note) {
        try {
            this.latestNote.setText(String.format("%s : %s", note.getAuthor().getLogin(), note.getText()));
        } catch (Exception e) {
            this.latestNote.setText("There are no notes yet");
        }
    }
    
    public void selectUser(User user) {
        this.assignedUsers.setSelectedKey(user);
    }

    public void setAllToNull() {
        this.currentWorkItem = null;
        this.loadAssignedUsers(null);
        this.setDeadline(null);
        this.setDescription("");
        this.setLastNote(null);
        this.setLastModified(null);
        this.setParent(0);
        this.setDeadline(null);
        
        save.setEnabled(false);
        addNote.setEnabled(false);
    }

    public void loadFromWorkItem(WorkItem wi, TabContent tc) {
        setAllToNull();
        
        this.tc = tc;
        this.currentWorkItem = wi;

        if (wi != null) {
            NoteCollection nc = wi.getNoteCollection();
            Collections.sort(nc);
            Project p = wi.getProject();
            User u = wi.getAssignedUser();
            WorkItem pwi = wi.getParent();
    
            setDescription(wi.getDescription());
            setParent((pwi != null) ? (pwi.getId()) : 0);
            setDeadline(wi.getExpectedTimestamp());
            setLastModified(wi.getLastModifiedTimestamp());
            loadAssignedUsers((p != null) ? (p.getAssignedUsers()) : null);
            selectUser((u != null) ? (u) : null);
            setPriority(wi.getPriority());
            setStatus(wi.getStatus());
            setLastNote((nc != null && nc.size() > 0) ? (nc.get(0)) : null);

            save.setEnabled(true);
            addNote.setEnabled(true);
        }
    }
    
    public void setWorkItemNoteHistoryTable(WorkItemNoteHistoryTable winht) {
        this.winht = winht;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == save) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure?") == 0) {
                boolean save = true;
                
                if (!deadline.getText().equals("00.00.0000 00:00") && (deadline.getText() != null)) {
                    try {
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        currentWorkItem.setExpectedTimestamp(df.parse(deadline.getText()));
                    }
                    catch (Exception e) {
                        save = false;
                        JOptionPane.showMessageDialog(this, "Incorrect date format");
                        LoggingFacade.handleException(e);
                    }
                }
                
                if (Integer.parseInt(parent.getText()) != 0) {
                    try {
                        WorkItem pwi = tc.getWorkItemCollection().getByPrimaryKey(Integer.parseInt(parent.getText()));
                        if (pwi != null) {
                            currentWorkItem.setParentWorkItem(pwi);
                        }
                        else {
                            throw new Exception("WorkItem does not exist");
                        }
                    }
                    catch (Exception e) {
                        save = false;
                        JOptionPane.showMessageDialog(this, "Check if Work item exists");
                        LoggingFacade.handleException(e);
                    }
                }
                else {
                    currentWorkItem.setParentWorkItem(null);
                }
                
                if (save) {                
                    currentWorkItem.setDescription(this.description.getText());
                    currentWorkItem.setAssignedUser(assignedUsers.getSelectedKey());
                    currentWorkItem.setStatus(statuses.getSelectedKey());
                    currentWorkItem.setPriority(priorities.getSelectedKey());
                    currentWorkItem.setLastModifiedTimestamp(Calendar.getInstance().getTime());

                    WorkItemDal dal = new WorkItemDal(tc.getConnectionInfo(), tc.getContext());
                    dal.update(tc.getWorkItemCollection());
                    tc.dataRefresh();
                }
            }
        }
        
        if (ae.getSource() == addNote) {
            final JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);     
            scrollPane.setPreferredSize(new Dimension(350, 150));
            
            JTextArea ta = new JTextArea();
            ta.setLineWrap(true);
            ta.setPreferredSize(new Dimension(500,200));
            
            Object[] msg = {"Enter new message", new JScrollPane(ta)};
            int result = JOptionPane.showConfirmDialog(this, msg, "NOTWA - Add new note", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION) {
                if(!ta.getText().equals("")) {
                    NoteCollection nc = currentWorkItem.getNoteCollection();
                    Note note = new Note(currentWorkItem.getId());
                    note.registerWithContext(tc.getContext());
                    User user = new User(tc.getCurrentCredentinals().getUserId());
                    user.setLogin(tc.getCurrentCredentinals().getLogin());
                    note.setAuthor(user);
                    note.setNoteText(ta.getText());
                    note.setInserted(true);
                    nc.add(note);
                    
                    NoteDal nd = new NoteDal(tc.getConnectionInfo(), tc.getContext());
                    nd.update(nc);
                    
                    this.loadFromWorkItem(currentWorkItem, tc);
                    winht.loadFromWorkItem(currentWorkItem);
                }
            }
        }
    }
}
