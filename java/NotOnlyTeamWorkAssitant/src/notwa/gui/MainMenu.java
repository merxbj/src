package notwa.gui;

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

public class MainMenu extends JMenuBar implements ActionListener {
    private JMenu menu;
    private JMenuItem mItemSyncAndRefresh,mItemExit,mItemConfigure;
    private JCheckBoxMenuItem cbWorkOffline;
    
    public MainMenu() {
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        this.add(menu);

        mItemSyncAndRefresh = new JMenuItem("Synchronize & Refresh", KeyEvent.VK_F5);
        mItemSyncAndRefresh.setAccelerator(KeyStroke.getKeyStroke("F5"));
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
        this.add(menu);

        mItemConfigure = new JMenuItem("Configure", KeyEvent.VK_C);
        mItemConfigure.addActionListener(this);
        menu.add(mItemConfigure);
        
        /*
         * Add search panel to MainMenu
         */
        this.add(Box.createHorizontalGlue());

        this.add(new JLabel("| Search "));
        
        this.add(this.addSearchField());
    }

    private JTextField addSearchField() {
        final JTextField searchField = new JTextField("Type here ...");
        
        Dimension searchFieldSize = new Dimension(200,20); 
        searchField.setMinimumSize(searchFieldSize);
        searchField.setMaximumSize(searchFieldSize);
        searchField.setPreferredSize(searchFieldSize);
        searchField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getSource() == searchField) {
                    if (searchField.getText().equals("Type here ...")) {
                        searchField.setText("");
                    }
                }
            }
        });
        
        return searchField;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        //TODO: add remaining actions
        if (ae.getSource() == mItemConfigure) {
            SettingsDialog sd = new SettingsDialog();
            sd.initSettingsDialog();
        }

        if (ae.getSource() == mItemExit)    {
            System.exit(-1);
        }
    }
}
