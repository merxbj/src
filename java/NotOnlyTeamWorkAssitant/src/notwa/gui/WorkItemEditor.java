/*
 * WorkItemEditor
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
 
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import notwa.common.ConnectionInfo;
import notwa.dal.ProjectDal;
import notwa.dal.ProjectToUserAssignmentDal;
import notwa.dal.UserDal;
import notwa.dal.UserToProjectAssignmentDal;
import notwa.dal.WorkItemDal;
import notwa.exception.ContextException;
import notwa.logger.LoggingFacade;
import notwa.wom.Context;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;
 
public class WorkItemEditor extends JDialog implements ActionListener {
    private JComboBox existingProjects,users,priorities,states;
    private JTextField newProjectName = new JTextField();
    private JTextField subject = new JTextField();
    private JTextField eParentId = new JTextField();
    private JTextField eExpectingDate = new JTextField();
    private JTextArea eDescription;
    private JButton okButton, stornoButton, chooseColorButton;
    private ConnectionInfo ci;
    private Context context;
    private WorkItemCollection wic;
    private boolean close = true;
    
    public WorkItemEditor(ConnectionInfo ci, Context context, WorkItemCollection wic) {
        this.ci = ci;
        this.context = context;
        this.wic = wic;
    }
    
    public void initAddDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Add");
        this.setLayout(new BorderLayout());
        this.setSize(750,400);

        JPanel jp = new JPanel();
        jp.setLayout(null);

        JLabel lExistingProject = new JLabel("Attach to existing project");
        lExistingProject.setBounds(63, 5, 152, 22);
        jp.add(lExistingProject);
        jp.add(loadExistingProjects());
        
        JLabel lCreateProject = new JLabel("Create new Project");
        lCreateProject.setBounds(63, 39, 124, 15);
        jp.add(lCreateProject);
        newProjectName.setBounds(227, 36, 138, 22);
        jp.add(newProjectName);
        newProjectName.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        SwitchComboBox();
                    }
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        SwitchComboBox();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        SwitchComboBox();
                    }
                });
        
        chooseColorButton = new JButton("Browse");
        chooseColorButton.setBounds(377, 36, 103, 22);
        chooseColorButton.setText("choose color");
        chooseColorButton.addActionListener(this);
        jp.add(chooseColorButton);

        JLabel lUser = new JLabel("User");
        lUser.setBounds(120, 212, 56, 15);
        jp.add(lUser);
        jp.add(loadUsers());
        
        JLabel lSubject = new JLabel("Subject");
        lSubject.setBounds(63, 96, 78, 15);
        jp.add(lSubject);
        jp.add(subject);
        subject.setBounds(227, 93, 138, 22);

        JLabel lPriority = new JLabel("Priority");
        lPriority.setBounds(63, 212, 56, 15);
        jp.add(lPriority);
        jp.add(loadWorkItemPriorties());
        
        JLabel lState = new JLabel("State");
        lState.setBounds(63, 240, 50, 15);
        jp.add(lState);
        jp.add(loadWorkItemStates());
        
        JLabel lDescription = new JLabel("Description");
        lDescription.setBounds(63, 123, 84, 15);
        jp.add(lDescription);
        eDescription = new JTextArea();
        eDescription.setBorder(BorderFactory.createEtchedBorder());
        eDescription.setBounds(227, 120, 458, 76);
        jp.add(eDescription);
        
        JLabel lParent = new JLabel("Parent WIT ID");
        lParent.setBounds(63, 68, 91, 15);
        jp.add(lParent);
        eParentId = new JTextField("0");
        eParentId.setBounds(227, 65, 54, 22);
        jp.add(eParentId);
        
        JLabel lExpectingDate = new JLabel("Expecting date");
        lExpectingDate.setBounds(63, 267, 101, 15);
        jp.add(lExpectingDate);
        eExpectingDate = new JTextField("00.00.0000 00:00");
        eExpectingDate.setBounds(227, 264, 138, 22);
        jp.add(eExpectingDate);
        
        this.add(jp, BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setLocationRelativeTo(null);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
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
    
    private JComboBox loadUsers() {
        users = new JComboBox();
        users.setBounds(350, 5, 138, 22);

        UserDal ud = new UserDal(ci, context);
        UserCollection uc = new UserCollection(context);
        ud.fill(uc);
        for (User user : uc) {
            users.addItem(new JAnyItemCreator(user, user.getLogin()));
        }
        
        return users;
    }
    
    private JComboBox loadExistingProjects() {
        existingProjects = new JComboBox();
        existingProjects.setBounds(227, 5, 138, 22);

        ProjectDal pd = new ProjectDal(ci, context);
        ProjectCollection pc = new ProjectCollection(context);
        pd.fill(pc);
        for (Project p : pc) {
            existingProjects.addItem(new JAnyItemCreator(p, p.getName()));
        }
        
        return existingProjects;
    }
    
    private JComboBox loadWorkItemStates() {
        states = new JComboBox();
        states.setBounds(227, 236, 138, 22);
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            states.addItem(new JAnyItemCreator(WorkItemStatus.values()[s],
                                                    WorkItemStatus.values()[s].toString()));
        }
        
        return states;
    }
    
    private JComboBox loadWorkItemPriorties() {
        priorities = new JComboBox();
        priorities.setBounds(227, 208, 138, 22);
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priorities.addItem(new JAnyItemCreator(WorkItemPriority.values()[p],
                                                        WorkItemPriority.values()[p].toString()));
        }

        return priorities;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            WorkItem wi = new WorkItem();
            wi.setAssignedUser((User)((JAnyItemCreator)users.getSelectedItem()).getAttachedObject());
            wi.setDescription(eDescription.getText());
            if (!eExpectingDate.getText().equals("00.00.0000 00:00")) {
                try {
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                    wi.setExpectedTimestamp((Date)df.parse((eExpectingDate.getText())));
                    close = true;
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Incorrect date format");
                    LoggingFacade.handleException(e);
                    close = false;
                }
            }
            wi.setLastModifiedTimestamp(Calendar.getInstance().getTime());
            if (Integer.parseInt(eParentId.getText()) != 0) {
                try {
                    WorkItem pwi = wic.getByPrimaryKey(Integer.parseInt(eParentId.getText()));
                    if (pwi != null) {
                        wi.setParentWorkItem(pwi);
                        close = true;
                    }
                    else {
                        throw new Exception("WorkItem does not exist");
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Check if Work item exists");
                    LoggingFacade.handleException(e);
                    close = false;
                }
            }
            wi.setPriority((WorkItemPriority)((JAnyItemCreator)priorities.getSelectedItem()).getAttachedObject());
            if (existingProjects.isEnabled()) {
                wi.setProject((Project)((JAnyItemCreator)existingProjects.getSelectedItem()).getAttachedObject());
            }
            else {
                Project project = new Project();
                project.registerWithContext(context);
                project.setProjectName(newProjectName.getText());
                try {
                    User user = (User)((JAnyItemCreator)users.getSelectedItem()).getAttachedObject();
                    user.setInserted(true);
                    
                    project.addAssignedUser(user);
                } catch (ContextException ex) {
                    JOptionPane.showMessageDialog(this, "New project creation has failed, check log for further information");
                    LoggingFacade.handleException(ex);
                    close = false;
                }
                
                ProjectCollection pc = new ProjectCollection(context);
                
                ProjectDal pd = new ProjectDal(ci, context);
                pd.fill(pc);
                pc.add(project);
                pd.update(pc);
                
                ProjectToUserAssignmentDal ptuad = new ProjectToUserAssignmentDal(ci, context);
                ptuad.update(project.getAssignedUsers());
                wi.setProject(project);
            }
            wi.setStatus((WorkItemStatus)((JAnyItemCreator)states.getSelectedItem()).getAttachedObject());
            wi.setSubject(subject.getText());
            wi.registerWithContext(context);

            wic.add(wi);
            
            WorkItemDal wid = new WorkItemDal(ci, context);
            wid.update(wic);
            
            if (close) {
                this.setVisible(false);
            }
        }

        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
        
        if(ae.getSource() == chooseColorButton) { //TODO all color choosing will be used this way
            JColorChooser colorChooser = new JColorChooser();
            JDialog jd = JColorChooser.createDialog( chooseColorButton,
                                        "Project color chooser",
                                        true,
                                        colorChooser,
                                        this,
                                        null);
            jd.setVisible(true); // not done yet 
        }
    }
    
    private void SwitchComboBox() {
        if (newProjectName.getText().isEmpty()) {
            existingProjects.setEnabled(true);
        }
        else {
            existingProjects.setEnabled(false);
        }
        
    }
}
