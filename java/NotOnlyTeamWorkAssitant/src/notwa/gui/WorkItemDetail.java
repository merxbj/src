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
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import notwa.common.EventHandler;

import notwa.wom.Note;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class WorkItemDetail extends WorkItemDetailLayout implements ActionListener {
    private static WorkItemDetail instance;
    private JButton save = new JButton("Save");
    private JTextArea description = new JTextArea();
    private JTextArea latestNote = new JTextArea();
    private JTextField parent = new JTextField();
    private JTextField deadline = new JTextField();
    private JTextField lastModified = new JTextField();
    private JComboBox status = new JComboBox();
    private JComboBox priority = new JComboBox();
    private JComboBox assignedUsers = new JComboBox();
    private EventHandler<GuiEvent> guiHandler;

    public WorkItemDetail() {
        init();
    }
    
    @Override
    public void init() {

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
            status.addItem(WorkItemStatus.values()[s]);
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
    
    public void setDescription(String description) {
        this.description.setText(description);
    }
    
    public void setParent(int id) {
        this.parent.setText(((Integer)id).toString());
    }
    
    public void setDeadline(String deadline) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = (Date)formatter.parse(deadline);
            this.deadline.setText(formatter.format(date));
        } catch (Exception e) { }
    }
    
    public void setLastModified(String lastModified) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = (Date)formatter.parse(lastModified);
            this.lastModified.setText(formatter.format(date));
        } catch (Exception e) { }
    }
    
    public void setPriority(WorkItemPriority wip) {
        this.priority.setSelectedItem(wip);
    }
    
    public void setStatus(WorkItemStatus wis) {
        this.status.setSelectedItem(wis);
    }
    
    public void setLastNote(Note note) {
        try {
            this.latestNote.setText(String.format("%s : %s",note.getAuthor().getLogin(), note.getText()));
        } catch (Exception e) {
            this.latestNote.setText("There are no notes yet");
        }
    }
    
    public void setAssignedUsers(UserCollection uc) {
        try {
            assignedUsers.removeAllItems();
            for (User user : uc) {
                assignedUsers.addItem(user.getLogin());
            }
        } catch (Exception e) {
            this.assignedUsers.addItem("unspecified");
        }
    }
    
    public void selectUser(String user) {
        this.assignedUsers.setSelectedItem(user);
    }

    public void setAllToNull() {
        this.setAssignedUsers(null);
        this.setDeadline("");
        this.setDescription("");
        this.setLastNote(null);
        this.setLastModified("");
        this.setParent(0);
        this.setPriority(null);
    }
}
