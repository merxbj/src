package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import notwa.common.ConnectionInfo;
import notwa.dal.WorkItemDal;
import notwa.wom.ContextManager;
import notwa.wom.WorkItemCollection;

public class MainLayoutLoader extends JPanel implements ActionListener {
    private JTabbedPane tabPanel;
    private JButton plusButton;
    
    public MainLayoutLoader () {
        //TODO: must be loaded from config - lastly used tabs(databases) etc.
    }

    public Component initMainLayout() {
        this.setLayout(new GridLayout(1,0));
        this.add(loadTabs());
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();

        //create empty tab, where we will attach new button
        tabPanel.addTab(null, new JLabel(   "Welcome to NOT Only Team Work Assistent -" +
        		                            "To beggin working, click on \"+\" button to login into Database")); 

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
        
        TabContent tc = new TabContent();
        tabPanel.insertTab(ci.getLabel(), null, tc.initTabContent(wic, ci), null, tabPanel.getTabCount()-1);
        tabPanel.setSelectedIndex(tabPanel.getTabCount()-2);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.initLoginDialog();
        }
    }
}
