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

import notwa.common.ApplicationSettings;
import notwa.common.Config;
import notwa.common.EventHandler;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableRowSorter;
import notwa.common.ConnectionInfo;
import notwa.gui.datamodels.WorkItemlModel;
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
        /*
         * This solves the problem with TextArea font in windows
         */
        UIManager.put("TextArea.font",new FontUIResource("monospaced",Font.PLAIN,12));

        /*
         * Show login dialog
         */
        invokeLogin(null);
        
        /*
         * Disable menu items that cannot be used until user is not logged
         */
        setMenuEnabled(false);
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
            case GuiEventParams.MENU_EVENT_PROJECT_MANAGEMENT:
                invokeProjectManagement(e.getParams());
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
                break;
            case GuiEventParams.DISABLE_MENU_ITEMS:
                setMenuEnabled(false);
                break;
            case GuiEventParams.ENABLE_MENU_ITEMS:
                setMenuEnabled(true);
                break;
            default:
                LoggingFacade.getLogger().logError("Unexpected event: %s", e.toString());
                break;
        }
    }

    public void handleSecurityEvent(SecurityEvent e) {
        switch (e.getEventId()) {
            case SecurityEventParams.SECURITY_EVENT_REQUEST_LOGIN:
                invokeLogin(e.getParams());
                break;
            default:
                LoggingFacade.getLogger().logError("Unexpected event: %s", e.toString());
                break;
        }
    }

    private void setMenuEnabled(boolean enabled) {
        this.menu.setMenuEnabled(enabled);
    }
    
    private void invokeConfigure(GuiEventParams params) {
        SettingsDialog sd = new SettingsDialog();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void invokeExit(GuiEventParams params) {
        System.exit(0);
    }

    private void invokeFiltering(GuiEventParams params) {
        FilteringDialog fd = new FilteringDialog();
    }

    private void invokeProjectManagement(GuiEventParams params) {
        ProjectManagement pm = new ProjectManagement(getActiveConnectionInfo(), getActivetContext());
    }
    
    private void invokeUserManagement(GuiEventParams params) {
        UserManagement um = new UserManagement(getActiveConnectionInfo(), getActivetContext());
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private void invokeTableRowSorterChanged(GuiEventParams params) {
        menu.setSorter((TableRowSorter<WorkItemlModel>) params.getParams());
    }

    private void trySetLookAndFeel() {
        try {
            ApplicationSettings as = Config.getInstance().getApplicationSettings();
            if (as.getSkin() != null || !as.getSkin().isEmpty()) {
                UIManager.setLookAndFeel(as.getSkin());
            }
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            }
            catch (Exception exc) {
                LoggingFacade.handleException(exc);
            }
        }
    }

    private void invokeLogin(SecurityEventParams params) {
        LoginDialog ld = new LoginDialog();
        LoginDialog.SignInParams sip = ld.getSignInParams();
        if ((sip.credentials != null) && (sip.credentials.isValid())) {
            IndeterminateProgressThread ipt = new IndeterminateProgressThread(new Action<LoginDialog.SignInParams>(sip) {

                @Override
                public void perform() {
                    mll.createWitView(params.connectionInfo, params.credentials);
                }
            }, statusBar);

            ipt.run();
        }
    }
}
