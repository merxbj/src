/*
 * TabContent
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
import notwa.dal.WorkItemDal;
import notwa.security.Credentials;
import notwa.sql.Parameter;
import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.Context;
import notwa.wom.ContextManager;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemStatus;

public class TabContent extends JPanel implements ActionListener {
    JButton addButton,showHideButton,showDepButton;
    private ConnectionInfo ci;
    private WorkItemDal dal;
    private Context currentContext;
    private WorkItemCollection wic;
    private ParameterSet ps;
    private WorkItemTable wiTable;
    private JComboBox userDefinedFiltersBox;
    private EventHandler<GuiEvent> guiHandler;
    private Credentials currentUser;

    public TabContent(ConnectionInfo ci, Credentials user) {
        init(ci, user, getDefaultParameters(user));
    }

    public TabContent(ConnectionInfo ci, Credentials user, ParameterSet ps) {
        init(ci, user, ps);
    }
    
    public void init(ConnectionInfo ci, Credentials user, ParameterSet ps) {
        this.ps = ps;
        this.ci = ci;
        
        currentContext = ContextManager.getInstance().newContext();
        currentUser = user;
        dal = new WorkItemDal(ci, currentContext);
        wic = new WorkItemCollection(currentContext);
        dal.fill(wic, ps);

        this.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        wiTable = new WorkItemTable(wic);
        wiTable.onFireSelectedRowChanged(new EventHandler<GuiEvent>() {

            @Override
            public void handleEvent(GuiEvent e) {
                if (guiHandler != null) {
                    guiHandler.handleEvent(e);
                }
            }
        });

        topPanel.add(this.initButtons(), BorderLayout.PAGE_START);
        topPanel.add(wiTable, BorderLayout.CENTER);
        
        this.add(topPanel, BorderLayout.CENTER);
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> handler) {
        this.guiHandler = handler;
    }
    
    private void fillUserDefinedFilterItems(JComboBox jcb) { // TODO
        jcb.addItem("Product = notwa");
        jcb.addItem("Status = IN_PROGRESS");
        jcb.addItem("Priority = critical");
        jcb.addItem("Configure ...");
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        /*TODO showDepButton = new JButton("Show dependency tree");*/
        
        addButton.addActionListener(this);
        
        leftButtonsPanel.add(addButton);
        
        buttonsPanel.add(leftButtonsPanel, BorderLayout.LINE_START);
        buttonsPanel.add(this.initFilteringComboBox(), BorderLayout.LINE_END);
        
        return buttonsPanel;
    }
    
    private JPanel initFilteringComboBox() {
        JPanel jp = new JPanel();
        
        userDefinedFiltersBox = new JComboBox();
        userDefinedFiltersBox.addActionListener(this);
        
        fillUserDefinedFilterItems(userDefinedFiltersBox);
        
        jp.add(userDefinedFiltersBox);
        
        return jp; 
    }
    
    public ConnectionInfo getConnectionInfo() {
        return ci;
    }

    public Context getContext() {
        return currentContext;
    }

    public WorkItemTable getWorkItemTable() {
        return wiTable;
    }
    
    public WorkItemCollection getWorkItemCollection() {
        return wic;
    }

    public void dataRefresh() {
        dal.refresh(wic, ps);
        wiTable.refresh();
    }

    public void refresh() {
        wiTable.refresh();
    }
    
    public Credentials getCurrentCredentinals() {
        return currentUser;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addButton) {
            WorkItemEditor wie = new WorkItemEditor(getConnectionInfo(), getContext(), getWorkItemCollection(), guiHandler, currentUser);
            wie.initAddDialog();
        }
        
        if (ae.getSource() == userDefinedFiltersBox) {
            if(userDefinedFiltersBox.getSelectedItem().equals("Configure ...")) {
                FilteringDialog fd = new FilteringDialog();
                fd.init();
            }
        }
    }

    private ParameterSet getDefaultParameters(Credentials user) {
        return new ParameterSet( new Parameter[] { 
            new Parameter(Parameters.WorkItem.ASSIGNED_USER, user.getUserId(), Sql.Relation.EQUALTY),
            new Parameter(Parameters.WorkItem.STATUS, WorkItemStatus.CLOSED.getValue(), Sql.Relation.NOT_EQUALS),
            new Parameter(Parameters.WorkItem.STATUS, WorkItemStatus.VERIFIED.getValue(), Sql.Relation.NOT_EQUALS)});
    }
}
