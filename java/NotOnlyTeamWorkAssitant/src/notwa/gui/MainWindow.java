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

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Class <code>MainWindow</code>
 *  
 * @author mrneo
 * @version %I% %G%
 */
public class MainWindow extends JFrame {
    private String version = new String("v0.5.0-r1"); //config?
    private static MainLayoutLoader mll = new MainLayoutLoader();
    
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
        
        initMainWindow();
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
        this.setJMenuBar(MainMenu.getInstance().initMainMenu());
        
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
    public static MainLayoutLoader getTabController() {
        return mll;
    }
}
