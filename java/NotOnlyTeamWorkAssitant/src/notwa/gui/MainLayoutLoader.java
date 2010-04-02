package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import notwa.common.ConnectionInfo;
import notwa.dal.WorkItemDal;
import notwa.wom.ContextManager;
import notwa.wom.WorkItemCollection;

public class MainLayoutLoader extends JComponent implements ActionListener,ChangeListener {
    private JTabbedPane tabPanel;
    private JButton plusButton;
    static JSplitPane sp;
    private TabContent tc;
    
    public MainLayoutLoader () {
        //TODO: must be loaded from config - lastly used tabs(databases) etc.
    }

    public Component initMainLayout() {
        this.setLayout(new GridLayout(1,0));
        sp = new JSplitPane(    JSplitPane.VERTICAL_SPLIT,
                loadTabs(), WorkItemDetailLayout.getInstance().initDetailLayout());
        sp.setResizeWeight(0.9);
        sp.setContinuousLayout(true);
        this.add(sp);
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();

        //create empty tab, where we will attach new button
        tabPanel.addTab(null, new JLabel(   "Welcome to NOT Only Team Work Assistent -" +
        		                            "To beggin working, click on \"+\" button to login into Database"));
        tabPanel.addChangeListener(this);
        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, this.initPlusButton());

        return tabPanel;        
    }
    
    private JButton initPlusButton() {
        plusButton = new JButton("+");
        plusButton.setBorder(null);
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);
        
        return plusButton;
    }

    public void createWitView(ConnectionInfo ci) {
        WorkItemCollection wic = new WorkItemCollection();
        wic.setCurrentContext(ContextManager.getInstance().newContext());
        WorkItemDal wid = new WorkItemDal(ci,wic.getCurrentContext());
        wid.Fill(wic);
        
        tc = new TabContent();
        tabPanel.insertTab(ci.getLabel(), null, tc.initTabContent(wic, ci), null, tabPanel.getTabCount()-1);
        tabPanel.setSelectedIndex(tabPanel.getTabCount()-2);
    }
    
    public static void hideDetail() {
        //TODO after fullscreen, is detail visible anyway ?!
        sp.setDividerLocation(50000);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.initLoginDialog();
        }
    }

    public TabContent getTabContent() {
        return tc;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        WorkItemDetail.getInstance().setAllToNull();
        WorkItemNoteHistoryTable.getInstance().setAllToNull();
    }
}
