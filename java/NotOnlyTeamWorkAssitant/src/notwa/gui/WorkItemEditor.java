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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
import notwa.dal.ProjectDal;
import notwa.dal.UserDal;
import notwa.dal.WorkItemDal;
import notwa.gui.components.KeyValueComboBox;
import notwa.logger.LoggingFacade;
import notwa.security.Credentials;
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
    private KeyValueComboBox<Project> projects;
    private KeyValueComboBox<User> users;
    private KeyValueComboBox<WorkItemPriority> priorities;
    private KeyValueComboBox<WorkItemStatus> statuses;
    private JTextField subject = new JTextField();
    private JTextField eParentId = new JTextField();
    private JFormattedTextField eExpectingDate = new JFormattedTextField();
    private JTextArea eDescription;
    private JButton okButton, stornoButton;
    private ConnectionInfo ci;
    private Context context;
    private WorkItemCollection wic;
    private boolean close = true;
    private EventHandler<GuiEvent> guiHandler;
    private Credentials currentUser;
    
    public WorkItemEditor(ConnectionInfo ci, Context context, WorkItemCollection wic, EventHandler<GuiEvent> guiHandler, Credentials currentUser) {
        this.ci = ci;
        this.context = context;
        this.wic = wic;
        this.guiHandler = guiHandler;
        this.currentUser = currentUser;
    }
    
    public void initAddDialog() {
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Add");
        this.setLayout(new BorderLayout());
        this.setSize(750,400);

        JPanel jp = new JPanel();
        jp.setLayout(null);

        JLabel lExistingProject = new JLabel("Project");
        lExistingProject.setBounds(63, 5, 152, 22);
        jp.add(lExistingProject);
        jp.add(loadExistingProjects());

        JLabel lUser = new JLabel("User");
        lUser.setBounds(63, 39, 124, 15);
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
        jp.add(loadWorkItemStatuses());
        
        JLabel lDescription = new JLabel("Description");
        lDescription.setBounds(63, 123, 84, 15);
        jp.add(lDescription);
        eDescription = new JTextArea();
        eDescription.setLineWrap(true);
        JScrollPane jsDescription = new JScrollPane(eDescription);
        jsDescription.setBounds(227, 120, 458, 76);
        jp.add(jsDescription);
        
        JLabel lParent = new JLabel("Parent WIT ID");
        lParent.setBounds(63, 68, 91, 15);
        jp.add(lParent);
        eParentId = new JTextField("0");
        eParentId.setBounds(227, 65, 54, 22);
        jp.add(eParentId);
        
        JLabel lExpectingDate = new JLabel("Expecting date");
        lExpectingDate.setBounds(63, 267, 101, 15);
        jp.add(lExpectingDate);

        jp.add(initeExpectingDate());
        
        this.add(jp, BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setLocationRelativeTo(null);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setVisible(true);
    }
    
    private JFormattedTextField initeExpectingDate() {
        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter("##.##.#### ##:##");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        JFormattedTextField eExpectingDate = new JFormattedTextField(mf);
        eExpectingDate.setBounds(227, 264, 138, 22);
        eExpectingDate.setValue("00.00.0000 00:00");
        
        return eExpectingDate;
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
    
    private KeyValueComboBox<User> loadUsers() {
        users = new KeyValueComboBox<User>();
        users.setBounds(227, 36, 138, 22);

        UserDal ud = new UserDal(ci, context);
        UserCollection uc = new UserCollection(context);
        ud.fill(uc);
        for (User user : uc) {
            users.addItem(user, user.getLogin());
        }
        
        users.setSelectedKey(new User(currentUser.getUserId()));
        
        return users;
    }
    
    private KeyValueComboBox<Project> loadExistingProjects() {
        projects = new KeyValueComboBox<Project>();
        projects.setBounds(227, 5, 138, 22);

        ProjectDal pd = new ProjectDal(ci, context);
        ProjectCollection pc = new ProjectCollection(context);
        pd.fill(pc);
        for (Project p : pc) {
            projects.addItem(p, p.getName());
        }
        
        return projects;
    }
    
    private  KeyValueComboBox<WorkItemStatus> loadWorkItemStatuses() {
        statuses = new KeyValueComboBox<WorkItemStatus>();
        statuses.setBounds(227, 236, 138, 22);
        for (WorkItemStatus wis : WorkItemStatus.values()) {
            statuses.addItem(wis, wis.toString());
        }
        
        return statuses;
    }
    
    private KeyValueComboBox<WorkItemPriority> loadWorkItemPriorties() {
        priorities = new KeyValueComboBox<WorkItemPriority>();
        priorities.setBounds(227, 208, 138, 22);
        for (WorkItemPriority wip : WorkItemPriority.values()) {
            priorities.addItem(wip, wip.toString());
        }

        return priorities;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            WorkItem wi = new WorkItem();
            wi.setAssignedUser(users.getSelectedKey());
            wi.setDescription(eDescription.getText());
            if (!eExpectingDate.getText().equals("") && !eExpectingDate.getText().equals("00.00.0000 00:00")) {
                try {
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
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
            wi.setPriority(priorities.getSelectedKey());
            wi.setProject(projects.getSelectedKey());
            wi.setStatus(statuses.getSelectedKey());
            wi.setSubject(subject.getText());
            wi.registerWithContext(context);

            if (close) {
                wic.add(wi);
                
                WorkItemDal wid = new WorkItemDal(ci, context);
                wid.update(wic);
                
                this.setVisible(false);
                GuiEventParams gep = new GuiEventParams(GuiEventParams.MENU_EVENT_SYNC_AND_REFRESH);
                guiHandler.handleEvent(new GuiEvent(gep));
            }
        }

        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }
}
