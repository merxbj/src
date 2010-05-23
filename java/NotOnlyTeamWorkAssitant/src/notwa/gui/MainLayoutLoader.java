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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
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
    private JMenuItem closeConnection;
    
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

        tabPanel.addMouseListener(new MouseAdapter() {
            private boolean handled = false;

            @Override
            public void mousePressed(MouseEvent me) {
                handled = invokeContextMenu(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (!handled) {
                    invokeContextMenu(me);
                }
            }
        });
        
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
        this.hideDetail();

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

        tabPanel.insertTab(new String(ci.getLabel() + " - " + credentials.getLogin() ), null, tc, null, tabPanel.getTabCount() - 1);
        tabPanel.setSelectedIndex(tabPanel.getTabCount() - 2);
    }
    
    public void hideDetail() {
        sp.setDividerLocation(50000);
    }
    
    public void showDetail() {
        sp.setDividerLocation(0.55);
    }
    
    private JPopupMenu initPopupMenu() {
        JPopupMenu jpm = new JPopupMenu();
        
        closeConnection = new JMenuItem("Close tab");
        closeConnection.addActionListener(this);
        
        jpm.add(closeConnection);
        
        return jpm;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            SecurityEventParams sep = new SecurityEventParams(SecurityEventParams.SECURITY_EVENT_REQUEST_LOGIN);
            securityHandler.handleEvent(new SecurityEvent(sep));
        }
        
        if (ae.getSource() == closeConnection) {
            tabPanel.remove(tabPanel.getSelectedIndex());
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
        TabContent activeTab = getActiveTab();
        GuiEventParams gep;
        if (activeTab != null) {
            gep = new GuiEventParams(GuiEventParams.TABLE_ROW_SORTER_CHANGED, getActiveTab().getWorkItemTable().getSorter());
            if (fireGuiEvent(new GuiEvent(gep))) {
                getActiveTab().refresh();
            }
        } else {
            widl.setDataToNull();
        }

        /*
         * Ensure that tools and similar MenuItems will be available only on tabs with connection
         */
        if (tabPanel.getTabCount()-1 == tabPanel.getSelectedIndex()) {
            gep = new GuiEventParams(GuiEventParams.DISABLE_MENU_ITEMS);
            this.hideDetail();
        }
        else {
            gep = new GuiEventParams(GuiEventParams.ENABLE_MENU_ITEMS);
            this.showDetail();
        }
        fireGuiEvent(new GuiEvent(gep));
        
        tryToSelectLastRow();
    }

    private boolean fireGuiEvent(GuiEvent ge) {
        if (guiHandler != null) {
            guiHandler.handleEvent(ge);
            return true;
        } else {
            return false;
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
            fireGuiEvent(ge);
        }
    }

    public synchronized void refreshDataOnActiveTab() {
        getActiveTab().dataRefresh();
        tryToSelectLastRow();
    }

    private void tryToSelectLastRow() {
        /*
         * Try to select lastly selected row, if tab is new automaticaly set first
         */
        try {
            getActiveTab().getWorkItemTable().selectRow(); 
        } catch (Exception e) {}        
    }
    
    private void invokeSelectedRowChanged(GuiEventParams params) {
        widl.onSelectedWorkItemChanged((WorkItem) params.getParams(), getActiveTab());
    }

    private boolean invokeContextMenu(MouseEvent me) {
        int index = tabPanel.getUI().tabForCoordinate(tabPanel, me.getX(), me.getY());

        /**
         * Check if
         *  Mouse was pressed only on Tab and
         *  Mouse event was Popup trigger and
         *  If its not last tab which is not closeable (the + button)
         */
        if ((index != -1) && me.isPopupTrigger() && (tabPanel.getTabCount() != tabPanel.getSelectedIndex() + 1)) {
            initPopupMenu().show(me.getComponent(), me.getX(), me.getY());
            return true;
        }

        return false;
    }
}
