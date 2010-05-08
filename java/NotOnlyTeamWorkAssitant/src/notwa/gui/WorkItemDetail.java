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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import notwa.dal.NoteDal;
import notwa.dal.WorkItemDal;
import notwa.gui.components.JAnyItemCreator;
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
    private JTextField deadline;
    private JTextField lastModified;
    private JComboBox status;
    private JComboBox priority;
    private JComboBox assignedUsers;
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
        this.deadline = new JTextField();
        this.lastModified = new JTextField();
        this.status = new JComboBox();
        this.priority = new JComboBox();
        this.assignedUsers = new JComboBox();
        
        this.setLayout(new BorderLayout(5,5));
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        
        JPanel pDescription = new JPanel();
        JLabel lDescription = new JLabel("Description");
        pDescription.add(lDescription);
        descriptionPanel.add(pDescription, BorderLayout.LINE_START);
        description.setBorder(BorderFactory.createEtchedBorder());
        descriptionPanel.add(description, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(5,5));

        topPanel.add(descriptionPanel, BorderLayout.CENTER);
        topPanel.add(this.initBoxes(), BorderLayout.LINE_END);
            
        this.add(topPanel, BorderLayout.CENTER);
            
        JPanel notePanel = new JPanel(new BorderLayout());

        JPanel pLatestNote = new JPanel();
        JLabel lLatestNote = new JLabel("Latest note");
        pLatestNote.add(lLatestNote);
        notePanel.add(pLatestNote, BorderLayout.LINE_START);
        latestNote.setBorder(BorderFactory.createEtchedBorder());
        notePanel.add(latestNote, BorderLayout.CENTER);
            
        JPanel buttonsPanel = new JPanel();
        
        save.addActionListener(this);
        addNote.addActionListener(this);
        
        buttonsPanel.add(addNote);
        buttonsPanel.add(save);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5,5));

        bottomPanel.add(notePanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.LINE_END);
        
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }
    
    private JPanel initBoxes() {
        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(null);
        boxesPanel.setPreferredSize(new java.awt.Dimension(401, 80));
        boxesPanel.setMinimumSize(new Dimension(0,70));

        JLabel lUser = new JLabel("User");
        boxesPanel.add(lUser);
        lUser.setBounds(5, 8, 45, 15);
        boxesPanel.add(assignedUsers);
        assignedUsers.setBounds(60, 4, 120, 22);
            
        JLabel lPriority = new JLabel("Priority");
        boxesPanel.add(lPriority);
        lPriority.setBounds(5, 33, 45, 15);
        JComboBox cbPriorities = this.loadWorkItemPriorties();
        boxesPanel.add(cbPriorities);
        priority.setBounds(60, 29, 120, 22);

        JLabel lStatus = new JLabel("Status");
        boxesPanel.add(lStatus);
        lStatus.setBounds(5, 59, 45, 15);
        JComboBox cbStatuses = this.loadWorkItemStatuses();
        boxesPanel.add(cbStatuses);
        status.setBounds(60, 55, 120, 22);

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
        
        return boxesPanel;
    }
    
    private JComboBox loadWorkItemStatuses() {
        status = new JComboBox();
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            status.addItem(new JAnyItemCreator(WorkItemStatus.values()[s], WorkItemStatus.values()[s].toString()));
        }
        
        return status;
    }
    
    private JComboBox loadWorkItemPriorties() {
        priority = new JComboBox();
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priority.addItem(new JAnyItemCreator(WorkItemPriority.values()[p], WorkItemPriority.values()[p].toString()));
        }

        return priority;
    }
        
    private void setAssignedUsers(UserCollection uc) {
        try {
            assignedUsers.removeAllItems();
            for (User user : uc) {
                assignedUsers.addItem(new JAnyItemCreator(user, user.getLogin()));
            }
        } catch (Exception e) {
            this.assignedUsers.addItem("unspecified");
        }
    }
    
    public void setDescription(String description) {
        this.description.setText(description);
    }
    
    public void setParent(int id) {
        this.parent.setText(((Integer)id).toString());
    }
    
    public void setDeadline(Date deadline) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            this.deadline.setText(formatter.format(deadline));
        } catch (Exception e) { 
            this.deadline.setText("0000-00-00 00:00");
        }
    }
    
    public void setLastModified(Date lastModified) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            this.lastModified.setText(formatter.format(lastModified));
        } catch (Exception e) {
            this.lastModified.setText("0000-00-00 00:00");
        }
    }
    
    public void setPriority(WorkItemPriority wip) {
        this.priority.setSelectedIndex(wip.ordinal());
    }
    
    public void setStatus(WorkItemStatus wis) {
        this.status.setSelectedIndex(wis.ordinal());
    }
    
    public void setLastNote(Note note) {
        try {
            this.latestNote.setText(String.format("%s : %s", note.getAuthor().getLogin(), note.getText()));
        } catch (Exception e) {
            this.latestNote.setText("There are no notes yet");
        }
    }
    
    public void selectUser(User user) {
        this.assignedUsers.setSelectedItem(user); //TODO 
    }

    public void setAllToNull() {
        this.currentWorkItem = null;
        this.setAssignedUsers(null);
        this.setDeadline(null);
        this.setDescription("");
        this.setLastNote(null);
        this.setLastModified(null);
        this.setParent(0);
        this.setDeadline(null);
    }

    public void loadFromWorkItem(WorkItem wi, TabContent tc) {
        setAllToNull();
        
        this.tc = tc;
        this.currentWorkItem = wi;

        NoteCollection nc = wi.getNoteCollection();
        Project p = wi.getProject();
        User u = wi.getAssignedUser();
        WorkItem pwi = wi.getParent();

        setDescription(wi.getDescription());
        setParent((pwi != null) ? (pwi.getId()) : 0);
        setDeadline(wi.getExpectedTimestamp());
        setLastModified(wi.getLastModifiedTimestamp());
        setAssignedUsers((p != null) ? (p.getAssignedUsers()) : null);
        selectUser((u != null) ? (u) : null);
        setPriority(wi.getPriority());
        setStatus(wi.getStatus());
        setLastNote((nc != null && nc.size() > 0) ? (wi.getNoteCollection().get(0)) : null);
    }
    
    public void setWorkItemNoteHistoryTable(WorkItemNoteHistoryTable winht) {
        this.winht = winht;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == save) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure?") == 0) {
                boolean save = true;
                
                if (!deadline.getText().equals("0000-00-00 00:00") && !(deadline.getText() != null)) {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        currentWorkItem.setExpectedTimestamp((Date)df.parse((deadline.getText())));
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
                    currentWorkItem.setAssignedUser((User)((JAnyItemCreator)assignedUsers.getSelectedItem()).getAttachedObject());
                    currentWorkItem.setStatus((WorkItemStatus)((JAnyItemCreator)status.getSelectedItem()).getAttachedObject());
                    currentWorkItem.setPriority((WorkItemPriority)((JAnyItemCreator)priority.getSelectedItem()).getAttachedObject());
                    currentWorkItem.setLastModifiedTimestamp(Calendar.getInstance().getTime());

                    WorkItemDal wid = new WorkItemDal(tc.getConnectionInfo(), tc.getContext());
                    wid.update(tc.getWorkItemCollection());
                    tc.dataRefresh();
                }
            }
        }
        
        if (ae.getSource() == addNote) {
            final JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);     
            scrollPane.setPreferredSize(new Dimension(350, 150));
            
            JTextArea ta = new JTextArea();
            ta.setPreferredSize(new Dimension(500,200));
            
            Object[] msg = {"Enter new message", ta};
            int result = JOptionPane.showConfirmDialog(this, msg, "NOTWA - Add new note", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION) {
                if(!ta.getText().equals("")) {
                    NoteCollection nc = currentWorkItem.getNoteCollection();
                    Note note = new Note(currentWorkItem.getId());
                    note.registerWithContext(tc.getContext());
                    note.setAuthor(currentWorkItem.getAssignedUser()); // TODO currentlyLoggedUser
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
