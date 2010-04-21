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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import notwa.common.ConnectionInfo;
import notwa.common.Event;
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
public class MainWindow extends JFrame implements EventHandler {
    private String version = new String("v0.5.0-r1"); //config?
    private MainLayoutLoader mll = new MainLayoutLoader(this);
    private List<EventHandler> handlers;

    /**
     * Constructor only calls method to initialize MainWindow
     */
    public MainWindow() {
        
        /*
         * temp this will be removed when loading from config is completly done
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // TODO DELETE
            e.printStackTrace();
        }
        /*
         * end.
         */
        initEventHandlers();
        initMainWindow();
        startup();
    }

    /**
     * Initialize MainWindow
     * 
     * 
     */
    private void initMainWindow() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent " + version.toString());
        this.setSize(1000,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /*
         * Load MainMenu
         */
        this.setJMenuBar(MainMenu.getInstance().initMainMenu(this));
        
        /*
         * create main JPanel and Load tabs 
         */
        this.add(mll.initMainLayout(), BorderLayout.CENTER);
        
        /*
         * create JStatusBar object
         */
        this.add(JStatusBar.getInstance(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    /**
     * Gets the tab controller.
     *
     * @return the tab controller
     */
    public MainLayoutLoader getTabController() {
        return mll;
    }
    
    /**
     * Processes that have to be done only at startup of application
     */
    private void startup() {
        mll.hideDetail(); // hide detail on startup - is unneeded
        
        /*
         * Show login dialog
         */
        LoginDialog ld = new LoginDialog(this);
        ld.initLoginDialog();
    }

    private ConnectionInfo getActiveConnectionInfo() {
        TabContent tc = mll.getActiveTab();
        return (tc == null) ? null : tc.getCurrentConnectionInfo();
    }

    private Context getActivetContext() {
        TabContent tc = mll.getActiveTab();
        return (tc == null) ? null : tc.getCurrentContext();
    }

    @Override
    public void handleEvent(Event e) {
        for (EventHandler handler : handlers) {
            handler.handleEvent(e);
        }
    }

    private void initEventHandlers() {
        handlers = new ArrayList<EventHandler>();

        handlers.add(new EventHandler() {
            @Override
            public void handleEvent(Event e) {
                if (e instanceof GuiEvent) {
                    handleMenuEvent((GuiEvent) e);
                }
            }
        });

        handlers.add(new EventHandler() {
            @Override
            public void handleEvent(Event e) {
                if (e instanceof SecurityEvent) {
                    handleSecurityEvent((SecurityEvent) e);
                }
            }
        });
    }

    public void handleMenuEvent(GuiEvent e) {
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
            case GuiEventParams.MENU_EVENT_NEW_USER:
                invokeNewUser(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_SYNC_AND_REFRESH:
                invokeSyncAndRefresh(e.getParams());
                break;
            case GuiEventParams.MENU_EVENT_USER_MANAGER:
                invokeUserManager(e.getParams());
                break;
            case GuiEventParams.ACTION_EVENT_HIDE_DETAIL:
                invokeHideDetail(e.getParams());
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
        sd.initSettingsDialog();
    }

    private void invokeExit(GuiEventParams params) {
        System.exit(0);
    }

    private void invokeFiltering(GuiEventParams params) {
        FilteringDialog fd = new FilteringDialog();
        fd.initFilteringDialog();
    }

    private void invokeNewUser(GuiEventParams params) {
        UserEditor ue = new UserEditor();
        ue.initEditorDialog();
    }

    private void invokeSyncAndRefresh(GuiEventParams params) {
        IndeterminateProgressThread ipt = new IndeterminateProgressThread(new Action() {
            @Override
            public void perform() {
                mll.refreshDataOnActiveTab();
            }
        });

        ipt.run();
    }

    private void invokeUserManager(GuiEventParams params) {
        UserManager um = new UserManager(getActiveConnectionInfo(), getActivetContext());
        um.initManagerDialog();
    }

    private void invokeHideDetail(GuiEventParams params) {
        getTabController().hideDetail();
    }

    private void invokeSuccessfulLogin(SecurityEventParams params) {
        getTabController().createWitView(params.getConnectionInfo(), params.getCredentials());
    }
}
