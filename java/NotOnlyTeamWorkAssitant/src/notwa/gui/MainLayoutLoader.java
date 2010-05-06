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
import notwa.logger.LoggingFacade;
import notwa.security.Credentials;
import notwa.security.SecurityEvent;
import notwa.security.SecurityEventParams;
import notwa.wom.WorkItem;

public class MainLayoutLoader extends JComponent implements ActionListener, ChangeListener {
    static JSplitPane sp;
    private JTabbedPane tabPanel;
    private JButton plusButton;
    private EventHandler<GuiEvent> guiHandler;
    private EventHandler<SecurityEvent> securityHandler;
    private WorkItemDetailLayout widl;
    
    public MainLayoutLoader () {
        init();
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> guiHandler) {
        this.guiHandler = guiHandler;
    }

    public void onFireSecurityEvent(EventHandler<SecurityEvent> securityHandler) {
        this.securityHandler = securityHandler;
    }

    public void init() {

        /**
         * Instantiate all GUI components
         */
        widl = new WorkItemDetailLayout();
        tabPanel = new JTabbedPane();
        plusButton = new JButton("+");
        sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabPanel, widl);

        /**
         * Setup the plus button
         */
        plusButton.setBorder(null);
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);

        /**
         * Setup the tab panel
         */
        tabPanel.addTab(null, new JLabel("Welcome to NOT Only Team Work Assistent - To beggin working, click on \"+\" button to login into Database"));
        tabPanel.addChangeListener(this);
        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, plusButton);

        /**
         * Setup the work item detail layout
         */
        widl.onFireGuiEvent(new EventHandler<GuiEvent>() {

            @Override
            public void handleEvent(GuiEvent e) {
                handleGuiEvent(e);
            }
        });

        /**
         * Setup the split pane
         */
        sp.setResizeWeight(0.9);
        sp.setContinuousLayout(true);

        /**
         * Setup this component
         */
        this.setLayout(new BorderLayout());
        this.add(sp);
    }

    public void createWitView(ConnectionInfo ci, Credentials credentials) {
        TabContent tc = new TabContent(ci, credentials);
        tc.onFireGuiEvent(new EventHandler<GuiEvent>() {

            @Override
            public void handleEvent(GuiEvent e) {
                handleGuiEvent(e);
            }
        });

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
            SecurityEventParams sep = new SecurityEventParams(SecurityEventParams.SECURITY_EVENT_REQUEST_LOGIN);
            securityHandler.handleEvent(new SecurityEvent(sep));
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
            GuiEventParams gep = new GuiEventParams(GuiEventParams.TABLE_ROW_SORTER_CHANGED, getActiveTab().getWorkItemTable().getSorter());
            guiHandler.handleEvent(new GuiEvent(gep));
            getActiveTab().refresh();
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
            widl.setDataToNull();
        }
    }

    private void handleGuiEvent(GuiEvent ge) {
        switch (ge.getEventId()) {
            case GuiEventParams.SELECTED_ROW_CHANGED:
                invokeSelectedRowChanged(ge.getParams());
                ge.setHandled(true);
                break;
            case GuiEventParams.ACTION_EVENT_HIDE_DETAIL:
                hideDetail();
                ge.setHandled(true);
                break;
        }

        if (!ge.isHandled()) {
            this.guiHandler.handleEvent(ge);
        }
    }

    public synchronized void refreshDataOnActiveTab() {
        getActiveTab().dataRefresh();
    }

    private void invokeSelectedRowChanged(GuiEventParams params) {
        widl.onSelectedWorkItemChanged((WorkItem) params.getParams(), getActiveTab().getConnectionInfo(), getActiveTab().getContext());
    }
}
