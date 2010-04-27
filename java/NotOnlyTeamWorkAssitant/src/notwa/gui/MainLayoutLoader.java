/*
 * MainLayoutLoader
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
import notwa.security.Credentials;
import notwa.security.SecurityEvent;
import notwa.sql.Parameter;
import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Sql;

//TODO: must be loaded from config - lastly used tabs(databases) etc.
public class MainLayoutLoader extends JComponent implements ActionListener, ChangeListener {
    static JSplitPane sp;
    private JTabbedPane tabPanel;
    private JButton plusButton;
    private EventHandler<GuiEvent> guiHandler;
    private EventHandler<SecurityEvent> securityHandler;
    
    public MainLayoutLoader () {
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> guiHandler) {
        this.guiHandler = guiHandler;
    }

    public void onFireSecurityEvent(EventHandler<SecurityEvent> securityHandler) {
        this.securityHandler = securityHandler;
    }

    public void init() {
        this.setLayout(new GridLayout(1,0));
        WorkItemDetailLayout widl = WorkItemDetailLayout.getInstance();
        if (widl != null) {
            widl.initDetailLayout();
            widl.onFireGuiEvent(guiHandler);
        }
        sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, loadTabs(), widl);
        sp.setResizeWeight(0.9);
        sp.setContinuousLayout(true);
        this.add(sp);
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();

        //create empty tab, where we will attach a new button
        tabPanel.addTab(null, new JLabel(   "Welcome to NOT Only Team Work Assistent -" +
        		                            "To beggin working, click on \"+\" button to login into Database"));
        tabPanel.addChangeListener(this);
        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, this.initPlusButton());

        return tabPanel;        
    }
    
    private JButton initPlusButton() {
        plusButton = new JButton("+");
        plusButton.setBorder(null);
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);
        
        return plusButton;
    }

    public void createWitView(ConnectionInfo ci, Credentials credentials) {
        TabContent tc = new TabContent(ci, new ParameterSet( new Parameter(Parameters.WorkItem.ASSIGNED_USER, credentials.getUserId(), Sql.Relation.EQUALTY)));
        tabPanel.insertTab(ci.getLabel(), null, tc, null, tabPanel.getTabCount() - 1);
        tabPanel.setSelectedIndex(tabPanel.getTabCount() - 2);
    }
    
    public void hideDetail() {
        //TODO after fullscreen, is detail visible anyway ?!
        sp.setDividerLocation(50000);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.onFireSecurityEvent(securityHandler);
        }
    }

    public TabContent getActiveTab() {
        Component comp = tabPanel.getComponentAt(tabPanel.getSelectedIndex());
        return (comp instanceof TabContent) ? (TabContent) comp : null;
    }

    /**
     * Sets currently used sorter by MainMenu to currently selected instance of WorkItemTable
     * @param ce
     */
    @Override
    public void stateChanged(ChangeEvent ce) {
        try {
            MainMenu.getInstance().setSorter(getActiveTab().getWorkItemTable().getSorter());
            getActiveTab().refresh();
        } catch (Exception e) {
            WorkItemDetail.getInstance().setAllToNull();
            WorkItemNoteHistoryTable.getInstance().setAllToNull();
        }
    }

    public synchronized void refreshDataOnActiveTab() {
        getActiveTab().dataRefresh();
    }
}
