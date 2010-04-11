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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import notwa.common.ConnectionInfo;
import notwa.dal.ProjectDal;
import notwa.dal.UserDal;
import notwa.wom.Context;
import notwa.wom.ContextManager;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;

public class UserManager extends JDialog implements ActionListener{
    private JButton okButton, stornoButton;
    private JComboBox projects;
    private JList users, currentlyAssignedUsers;
    private Context context;
    private ConnectionInfo ci = MainWindow.getTabController().getTabContent().getCurrentConnectionInfo();
    private DefaultListModel projectUsersModel = new DefaultListModel(); // TODO make our own abstractListModel to
    private DefaultListModel usersModel = new DefaultListModel();       // make possible to assign object to element
    private ArrayList<String> assignedUsers = new ArrayList<String>(); //used only for checking if user is already assigned to project
    
    public UserManager() {
        context = ContextManager.getInstance().newContext();
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
        JScrollPane allUsersPanel = new JScrollPane(users);
        allUsersPanel.setBounds(70, 77, 112, 131);
        componentsPanel.add(allUsersPanel);
        
        JLabel lAssignedUsers = new JLabel("Already assigned users");
        componentsPanel.add(lAssignedUsers);
        lAssignedUsers.setBounds(279, 52, 152, 22);
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
                usersModel.addElement(user.getLogin());
        }
        
        users = new JList(usersModel);
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
            projectUsersModel.addElement(user.getLogin());
            assignedUsers.add(user.getLogin());
        }

        currentlyAssignedUsers = new JList(projectUsersModel);
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
            this.getAllProjectUsers();
            currentlyAssignedUsers.updateUI();
            this.getAllUsers();
            users.updateUI();
        }
    }
}
