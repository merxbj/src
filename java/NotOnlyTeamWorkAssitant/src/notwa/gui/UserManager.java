/*
 * UserManager
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import notwa.common.ConnectionInfo;
import notwa.logger.LoggingFacade;
import notwa.dal.ProjectDal;
import notwa.dal.ProjectToUserAssignmentDal;
import notwa.dal.UserDal;
import notwa.dal.UserToProjectAssignmentDal;
import notwa.exception.ContextException;
import notwa.wom.Context;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;

//TODO on project change ask if changes may to be saved or not
public class UserManager extends JDialog implements ActionListener {
    private JButton okButton, stornoButton, addButton, removeButton;
    private JComboBox projects;
    private JList users, currentlyAssignedUsers;
    private Context context;
    private ConnectionInfo ci;
    private ListModel projectUsersModel = new ListModel();
    private ListModel usersModel = new ListModel();
    private ArrayList<String> assignedUsers = new ArrayList<String>(); //used only for checking if user is already assigned to project
    private Project currentlySelectedProject;
    
    public UserManager(ConnectionInfo ci, Context context) {
        this.context = context;
        this.ci = ci;
    }
    
    public void initManagerDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - User Manager");
        this.setSize(500,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(this.initMainComponents(),BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JPanel initMainComponents() {
        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(null);

        JLabel lProject = new JLabel("Project");
        componentsPanel.add(lProject);
        lProject.setBounds(149, 12, 61, 22);
        componentsPanel.add(getAllProjects());

        JLabel lAllRegisteredUsers = new JLabel("All registered users");
        componentsPanel.add(lAllRegisteredUsers);
        lAllRegisteredUsers.setBounds(75, 56, 135, 15);

        users = new JList(usersModel);
        JScrollPane allUsersPanel = new JScrollPane(users);
        allUsersPanel.setBounds(70, 77, 112, 131);
        componentsPanel.add(allUsersPanel);
        
        addButton = new JButton("Add >");
        addButton.addActionListener(this);
        removeButton = new JButton("< Remove");
        removeButton.addActionListener(this);
        componentsPanel.add(addButton);
        addButton.setBounds(194, 99, 75, 20);
        componentsPanel.add(removeButton);
        removeButton.setBounds(194, 124, 75, 20);

        JLabel lAssignedUsers = new JLabel("Already assigned users");
        componentsPanel.add(lAssignedUsers);
        lAssignedUsers.setBounds(279, 52, 152, 22);
        currentlyAssignedUsers = new JList(projectUsersModel);
        JScrollPane assignedUsersPanel = new JScrollPane(currentlyAssignedUsers);
        assignedUsersPanel.setBounds(288, 74, 114, 129);
        componentsPanel.add(assignedUsersPanel);
        
        return componentsPanel;
    }
    
    private JComboBox getAllProjects() {
        projects = new JComboBox();
        projects.setBounds(210, 14, 144, 18);
        projects.addActionListener(this);
        
        ProjectCollection pc = new ProjectCollection();
        pc.setCurrentContext(context);
        ProjectDal pDal = new ProjectDal(ci, pc.getCurrentContext());
        pDal.fill(pc);
        
        for (Project project : pc) {
            projects.addItem(new JComboBoxItemCreator(project, project.getName()));
        }
        
        return projects;
    }
    
    private void getAllUsers() {
        try {
            usersModel.clear(); // clear existing items in list
        }
        catch (Exception e) { }
        
        UserCollection uc = new UserCollection();
        uc.setCurrentContext(context);
        UserDal uDal = new UserDal(ci, uc.getCurrentContext());
        uDal.fill(uc);
        
        for (User user : uc) {
            if (!assignedUsers.contains(user.getLogin()))
                usersModel.addElement(new JListItemCreator(user, user.getLogin()));
        }
    }
    
    private void getAllProjectUsers() {
        try {
            projectUsersModel.clear(); // clear existing items in list
        }
        catch (Exception e) { }
        try {
            assignedUsers.clear();
        }
        catch (Exception e) { }
        
        UserCollection usersInProject = ((Project)((JComboBoxItemCreator)projects.getSelectedItem()).getAttachedObject()).getAssignedUsers();
        
        for (User user : usersInProject) {
            projectUsersModel.addElement(new JListItemCreator(user, user.getLogin()));
            assignedUsers.add(user.getLogin());
        }
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel();
        
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        buttonsPanel.add(okButton);
        buttonsPanel.add(stornoButton);
        
        return buttonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            this.setVisible(false);
        }
        
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
        
        if (ae.getSource() == projects) {
            if (    !assignedUsers.containsAll(Arrays.asList(projectUsersModel.toArray())) ||
                    !Arrays.asList(projectUsersModel.toArray()).containsAll(assignedUsers)) { // check if something has changed
                if (JOptionPane.showConfirmDialog(this, "Do you really want to save changes?") == 0) {
                    ProjectToUserAssignmentDal ptuad = new ProjectToUserAssignmentDal(ci, context);
                    ptuad.update(currentlySelectedProject.getAssignedUsers());
                }
            }
            
            this.getAllProjectUsers();
            try {
                currentlyAssignedUsers.updateUI();
            } catch (Exception e) { }
            this.getAllUsers();
            try {
                users.updateUI();
            } catch (Exception e) { }
        }
        
        if (ae.getSource() == addButton) {
            currentlySelectedProject = (Project)((JComboBoxItemCreator)projects.getSelectedItem()).getAttachedObject();
            if (users.getSelectedValues().length != 0) {
                for (int i=0; i<users.getSelectedValues().length+1; i++) {
                    try {
                        User user = (User)((JListItemCreator)users.getSelectedValues()[i]).getAttachedObject();
                        currentlySelectedProject.addAssignedUser(user);
                    } catch (Exception e) {
                        LoggingFacade.getInstanece().handleException(e);
                    }
                    projectUsersModel.addElement(users.getSelectedValues()[i]);
                    usersModel.removeElement(users.getSelectedValues()[i]);
                }
            }
        }
        
        if (ae.getSource() == removeButton) {
            currentlySelectedProject = (Project)((JComboBoxItemCreator)projects.getSelectedItem()).getAttachedObject();
            if (currentlyAssignedUsers.getSelectedValues().length != 0) {
                for (int i=0; i<currentlyAssignedUsers.getSelectedValues().length+1; i++) {
                    try {
                        User user = (User)((JListItemCreator)currentlyAssignedUsers.getSelectedValues()[i]).getAttachedObject();
                        currentlySelectedProject.removeAssignedUser(user);
                    } catch (Exception e) {
                        LoggingFacade.getInstanece().handleException(e);
                    }
                    usersModel.addElement(currentlyAssignedUsers.getSelectedValues()[i]);
                    projectUsersModel.removeElement(currentlyAssignedUsers.getSelectedValues()[i]);
                }
            }
        }
    }
}
