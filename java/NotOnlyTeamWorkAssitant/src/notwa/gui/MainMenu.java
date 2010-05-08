/*
 * MainMenu
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import notwa.gui.tablemodels.WorkItemlModel;

public class MainMenu extends JMenuBar implements ActionListener {
    private static final JTextField searchField = new JTextField("Type here ...");
    private JMenuItem mItemSyncAndRefresh;
    private JMenuItem mItemExit;
    private JMenuItem mItemConfigure;
    private JMenuItem mItemFiltering;
    private JMenuItem mItemUserManagement;
    private JMenuItem mItemProjectManagement;
    private JMenuItem mItemAssignmentManager;
    private JMenu menu;
    private JCheckBoxMenuItem cbWorkOffline;
    private TableRowSorter<WorkItemlModel> sorter;
    private EventHandler<GuiEvent> guiHandler;
    
    public MainMenu() {
        init();
    }
    
    public void init() {
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        this.add(menu);

        mItemSyncAndRefresh = new JMenuItem("Synchronize & Refresh", KeyEvent.VK_F5);
        mItemSyncAndRefresh.setAccelerator(KeyStroke.getKeyStroke("F5"));
        mItemSyncAndRefresh.addActionListener(this);
        menu.add(mItemSyncAndRefresh);

        menu.addSeparator();

        cbWorkOffline = new JCheckBoxMenuItem("Work offline");
        cbWorkOffline.setMnemonic(KeyEvent.VK_O);
        menu.add(cbWorkOffline);

        menu.addSeparator();

        mItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
        mItemExit.addActionListener(this);
        menu.add(mItemExit);

        /*
         * Build settings menu bar.
         */
        menu = new JMenu("Settings");
        menu.setMnemonic(KeyEvent.VK_S);
        
        mItemConfigure = new JMenuItem("Application settings", KeyEvent.VK_A);
        mItemConfigure.addActionListener(this);
        
        mItemFiltering = new JMenuItem("Configure Sorting / Filtering");
        mItemFiltering.addActionListener(this);
        
        menu.add(mItemConfigure);
        menu.add(mItemFiltering);
        
        this.add(menu);
        /*
         * Build tools menu bar.
         */
        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);

        mItemProjectManagement = new JMenuItem("Project management");
        mItemProjectManagement.addActionListener(this);
        
        mItemUserManagement = new JMenuItem("User management");
        mItemUserManagement.addActionListener(this);
        
        mItemAssignmentManager = new JMenuItem("Assignment manager");
        mItemAssignmentManager.addActionListener(this);
        
        menu.add(mItemProjectManagement);
        menu.add(mItemUserManagement);
        menu.add(mItemAssignmentManager);
        
        this.add(menu);
        
        /*
         * Add search panel to MainMenu
         */
        this.add(Box.createHorizontalGlue());
        this.add(new JLabel("| Search "));
        this.add(this.addSearchField());
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> guiHandler) {
        this.guiHandler = guiHandler;
    }

    private JTextField addSearchField() {
        Dimension searchFieldSize = new Dimension(200,20); 
        searchField.setMinimumSize(searchFieldSize);
        searchField.setMaximumSize(searchFieldSize);
        searchField.setPreferredSize(searchFieldSize);
        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (me.getSource() == searchField) {
                    if (searchField.getText().equals("Type here ...")) {
                        searchField.setText("");
                    }
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        createFilter();
                    }
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        createFilter();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        createFilter();
                    }
                });
        return searchField;
    }
    
    private void createFilter() {
        RowFilter<WorkItemlModel, Object> rf = null;
        try {
            rf = RowFilter.regexFilter(searchField.getText(),0,1,2,3,4,5);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        this.sorter.setRowFilter(rf);
    }
    
    public void setSorter(TableRowSorter<WorkItemlModel> sorter) {
        this.sorter = sorter;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == mItemConfigure) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_CONFIGURE)));
        } else if (ae.getSource() == mItemFiltering) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_FILTERING)));
        } else if (ae.getSource() == mItemProjectManagement) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_PROJECT_MANAGEMENT)));
        } else if (ae.getSource() == mItemUserManagement) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_USER_MANAGEMENT)));
        } else if (ae.getSource() == mItemAssignmentManager) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_ASSIGNMENT_MANAGER)));
        } else if (ae.getSource() == mItemExit) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_EXIT)));
        } else if (ae.getSource() == mItemSyncAndRefresh) {
            guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.MENU_EVENT_SYNC_AND_REFRESH)));
        }
    }

}
