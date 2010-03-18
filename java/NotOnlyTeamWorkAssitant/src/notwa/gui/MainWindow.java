package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainWindow extends JFrame {
    private String version = new String("v0.0.1-r1"); //config?
    private static MainLayoutLoader mll = new MainLayoutLoader();
    
    public MainWindow() {
        
        /*
         * temp this will be removed when loading from config is completly done
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * end.
         */
        
        initMainWindow();
    }

    private void initMainWindow() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent " + version.toString());
        this.setSize(1000,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /*
         * Load MainMenu
         */
        MainMenu mm = new MainMenu();
        this.setJMenuBar(mm);
        
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
    
    public static MainLayoutLoader getTabController() {
        return mll;
    }
}
