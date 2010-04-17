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
import notwa.sql.Parameter;
import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Sql;

//TODO: must be loaded from config - lastly used tabs(databases) etc.
public class MainLayoutLoader extends JComponent implements ActionListener, ChangeListener {
    static JSplitPane sp;
    private JTabbedPane tabPanel;
    private JButton plusButton;
    private EventHandler handler;
    
    public MainLayoutLoader (EventHandler handler) {
        this.handler = handler;
    }

    public Component initMainLayout() {
        this.setLayout(new GridLayout(1,0));
        sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, loadTabs(), WorkItemDetailLayout.getInstance().initDetailLayout(handler));
        sp.setResizeWeight(0.9);
        sp.setContinuousLayout(true);
        this.add(sp);
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();

        //create empty tab, where we will attach new button
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
        TabContent tc = new TabContent(ci, new ParameterSet( new Parameter(Parameters.WorkItem.ASSIGNED_USER, credentials.getUserId(), Sql.Condition.EQUALTY)));
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
            LoginDialog ld = new LoginDialog(handler);
            ld.initLoginDialog();
        }
    }

    public TabContent getActiveTab() {
        return (TabContent) tabPanel.getComponentAt(tabPanel.getSelectedIndex());
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

    public synchronized void refreshActiveTab() {
        getActiveTab().refresh();
    }
}
