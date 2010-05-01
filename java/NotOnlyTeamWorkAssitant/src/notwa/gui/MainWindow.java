/*
 * MainWindow
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

import notwa.common.EventHandler;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.table.TableRowSorter;
import notwa.common.ConnectionInfo;
import notwa.logger.LoggingFacade;
import notwa.security.SecurityEvent;
import notwa.security.SecurityEventParams;
import notwa.threading.Action;
import notwa.threading.IndeterminateProgressThread;
import notwa.wom.Context;

/**
 * Class <code>MainWindow</code>
 *  
 * @author mrneo
 * @version %I% %G%
 */
public class MainWindow extends JFrame {

    private MainLayoutLoader mll;
    private MainMenu menu;
    private JStatusBar statusBar;
    /**
     * Constructor only calls method to initialize MainWindow
     */
    public MainWindow() {
        trySetLookAndFeel();
        init();
        startup();
    }

    /**
     * Performs the initialization
     */
    private void init() {

        /**
         * Instantiate all GUI components
         */
        this.statusBar = new JStatusBar();
        this.menu = new MainMenu();
        this.mll = new MainLayoutLoader();

        /**
         * Setup the menu
         */
        menu.onFireGuiEvent(new EventHandler<GuiEvent>() {
            @Override
            public void handleEvent(GuiEvent e) {
                handleGuiEvent(e);
            }
        });

        /**
         * Setup the main layout loader
         */
        mll.onFireGuiEvent(new EventHandler<GuiEvent>() {
            @Override
            public void handleEvent(GuiEvent e) {
                handleGuiEvent(e);
            }
        });

        mll.onFireSecurityEvent(new EventHandler<SecurityEvent>() {
            @Override
            public void handleEvent(SecurityEvent e) {
                handleSecurityEvent(e);
            }
        });
        
        /*
         * Setup this component
         */
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent ");
        this.setSize(1000,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(statusBar, BorderLayout.PAGE_END);
        this.add(mll, BorderLayout.CENTER);
        this.setJMenuBar(menu);
        this.setVisible(true);
    }
    
    /**
     * Processes that have to be taken only during the startup of the application
     */
    private void startup() {
        mll.hideDetail(); // hide detail on startup - is unneeded
        
        /*
         * Show login dialog
         */
        LoginDialog ld = new LoginDialog();
        ld.onFireSecurityEvent(new EventHandler<SecurityEvent>() {
            @Override
            public void handleEvent(SecurityEvent e) {
                handleSecurityEvent(e);
            }
        });
    }

    private ConnectionInfo getActiveConnectionInfo() {
        TabContent tc = mll.getActiveTab();
        return (tc == null) ? null : tc.getConnectionInfo();
    }

    private Context getActivetContext() {
        TabContent tc = mll.getActiveTab();
        return (tc == null) ? null : tc.getContext();
    }

    public void handleGuiEvent(GuiEvent e) {
        switch (e.getEventId()) {
            case GuiEventParams.MENU_EVENT_CONFIGURE:
                invokeConfigure(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_EXIT:
                invokeExit(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_FILTERING:
                invokeFiltering(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_USER_MANAGEMENT:
                invokeUserManagement(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_SYNC_AND_REFRESH:
                invokeSyncAndRefresh(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_ASSIGNMENT_MANAGER:
                invokeAssignmentManager(e.getParams());
                break;
            case GuiEventParams.TABLE_ROW_SORTER_CHANGED:
                invokeTableRowSorterChanged(e.getParams());
            default:
                LoggingFacade.getLogger().logError("Unexpected event: %s", e.toString());
                break;
        }
    }

    public void handleSecurityEvent(SecurityEvent e) {
        switch (e.getEventId()) {
            case SecurityEventParams.SECURITY_EVENT_SUCCESSFUL_LOGIN:
                invokeSuccessfulLogin(e.getParams());
                break;
            default:
                LoggingFacade.getLogger().logError("Unexpected event: %s", e.toString());
                break;
        }
    }

    private void invokeConfigure(GuiEventParams params) {
        SettingsDialog sd = new SettingsDialog();
    }

    private void invokeExit(GuiEventParams params) {
        System.exit(0);
    }

    private void invokeFiltering(GuiEventParams params) {
        FilteringDialog fd = new FilteringDialog();
    }

    private void invokeUserManagement(GuiEventParams params) {
        UserManagement um = new UserManagement(getActiveConnectionInfo(), getActivetContext());
    }

    private void invokeSyncAndRefresh(GuiEventParams params) {
        IndeterminateProgressThread ipt = new IndeterminateProgressThread(new Action() {
            @Override
            public void perform() {
                mll.refreshDataOnActiveTab();
            }
        }, statusBar);

        ipt.run();
    }

    private void invokeAssignmentManager(GuiEventParams params) {
        AssignmentManager um = new AssignmentManager(getActiveConnectionInfo(), getActivetContext());
    }

    private void invokeSuccessfulLogin(SecurityEventParams params) {
        mll.createWitView(params.getConnectionInfo(), params.getCredentials());
    }

    private void invokeTableRowSorterChanged(GuiEventParams params) {
        menu.setSorter((TableRowSorter<TblModel>) params.getParams());
    }

    private void trySetLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }
}
