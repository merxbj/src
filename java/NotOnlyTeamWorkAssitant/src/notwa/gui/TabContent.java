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
import javax.swing.JComponent;
import javax.swing.JPanel;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
import notwa.dal.WorkItemDal;
import notwa.sql.ParameterSet;
import notwa.wom.Context;
import notwa.wom.ContextManager;
import notwa.wom.WorkItemCollection;

public class TabContent extends JComponent implements ActionListener {
    JButton addButton,showHideButton,showDepButton;
    private ConnectionInfo ci;
    private WorkItemDal dal;
    private Context currentContext;
    private WorkItemCollection wic;
    private ParameterSet ps;
    private WorkItemTable wiTable;
    private JComboBox userDefinedFiltersBox, defaultSortBox;
    private EventHandler<GuiEvent> guiHandler;

    //TODO: create new context menu on every TAB - 1. menu item - Close connection
    //TODO: both must have parameter to know what information we want to show
    public TabContent(ConnectionInfo ci) {
        init(ci, new ParameterSet());
    }

    public TabContent(ConnectionInfo ci, ParameterSet ps) {
        init(ci, ps);
    }
    
    public void init(ConnectionInfo ci, ParameterSet ps) {
        currentContext = ContextManager.getInstance().newContext();
        dal = new WorkItemDal(ci, currentContext);
        wic = new WorkItemCollection(currentContext);
        this.ps = ps;
        dal.fill(wic, ps);

        this.ci = ci;
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
    
    private void fillDefaultSortingItems(JComboBox jcb) { // TODO
        jcb.addItem("Product");
        jcb.addItem("Status");
        jcb.addItem("Priority");
    }
    
    private void fillUserDefinedFilterItems(JComboBox jcb) { // TODO
        jcb.addItem("Product = notwa");
        jcb.addItem("Status = IN_PROGRESS");
        jcb.addItem("Priority = critical");
        jcb.addItem("Configure ..."); //TODO same ref as mainMenu>Configure sort/filter
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        /*TODO showDepButton = new JButton("Show dependency tree");*/
        
        addButton.addActionListener(this);
        
        leftButtonsPanel.add(addButton);
        
        buttonsPanel.add(leftButtonsPanel, BorderLayout.LINE_START);
        buttonsPanel.add(this.initFilteringComboBoxes(), BorderLayout.LINE_END);
        
        return buttonsPanel;
    }
    
    private JPanel initFilteringComboBoxes() {
        JPanel jp = new JPanel();
        
        defaultSortBox = new JComboBox();
        defaultSortBox.addActionListener(this);
        userDefinedFiltersBox = new JComboBox();
        userDefinedFiltersBox.addActionListener(this);
        
        fillDefaultSortingItems(defaultSortBox);
        fillUserDefinedFilterItems(userDefinedFiltersBox);
        
        jp.add(defaultSortBox);
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
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == addButton) {
            WorkItemEditor wie = new WorkItemEditor(getConnectionInfo(), getContext(), getWorkItemCollection());
            wie.initAddDialog();
        }
        
        if(ae.getSource() == userDefinedFiltersBox) {
            if(userDefinedFiltersBox.getSelectedItem().equals("Configure ...")) {
                FilteringDialog fd = new FilteringDialog();
                fd.init();
            }
        }
    }
}
