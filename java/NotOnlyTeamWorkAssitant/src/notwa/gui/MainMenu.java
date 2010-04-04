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
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class MainMenu extends JMenuBar implements ActionListener {
    private static MainMenu instance;
    private JMenu menu;
    private JMenuItem mItemSyncAndRefresh,mItemExit,mItemConfigure,mItemFiltering;
    private JCheckBoxMenuItem cbWorkOffline;
    private final JTextField searchField = new JTextField("Type here ...");
    private TableRowSorter<TblModel> sorter;
    
    public static MainMenu getInstance() {
        if (instance == null) {
            instance = new MainMenu();
        }
        return instance;
    }
    
    private MainMenu() { }
    
    public MainMenu initMainMenu() {
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

        mItemConfigure = new JMenuItem("Application settings", KeyEvent.VK_A);
        mItemConfigure.addActionListener(this);
        
        mItemFiltering = new JMenuItem("Configure Sorting / Filtering");
        mItemFiltering.addActionListener(this);
        
        menu.add(mItemConfigure);
        menu.add(mItemFiltering);
        
        /*
         * Add search panel to MainMenu
         */
        this.add(Box.createHorizontalGlue());

        this.add(new JLabel("| Search "));
        
        this.add(this.addSearchField());
        
        return this;
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
        RowFilter<TblModel, Object> rf = null;
        try {
            rf = RowFilter.regexFilter(searchField.getText(),0,1,2,3,4,5);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        this.sorter.setRowFilter(rf);
    }
    
    public void setSorter(TableRowSorter<TblModel> sorter) {
        this.sorter = sorter;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        //TODO: add remaining actions
        if (ae.getSource() == mItemConfigure) {
            SettingsDialog sd = new SettingsDialog();
            sd.initSettingsDialog();
        }
        
        if (ae.getSource() == mItemFiltering) {
            FilteringDialog fd = new FilteringDialog();
            fd.initFilteringDialog();
        }

        if (ae.getSource() == mItemExit) {
            System.exit(-1);
        }
    }
}
