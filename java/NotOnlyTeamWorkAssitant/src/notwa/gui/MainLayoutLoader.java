package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import notwa.common.ConnectionInfo;
import notwa.dal.WorkItemDal;
import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class MainLayoutLoader extends JPanel implements ActionListener {
    private JTabbedPane tabPanel;
    private JButton plusButton;
    
    public MainLayoutLoader () {
    }

    public Component initMainLayout() {
        this.setLayout(new GridLayout(1,0));
        this.add(loadTabs());
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();
        
        //TODO: must be loaded from config - lastly used tabs(databases)
        TabContent tc = new TabContent();
        tabPanel.addTab("Default", tc.initTabContent(null));
        
        tabPanel.addTab(null,null); //create empty tab, where we will add new button

        plusButton = new JButton("+");
        plusButton.setBorder(null);
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);
        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, plusButton);

        return tabPanel;        
    }
    
    public void createWitView(ConnectionInfo ci) {
        WorkItemCollection wic = new WorkItemCollection();
        WorkItemDal wid = new WorkItemDal(ci);
        wid.Fill(wic);
        
        TabContent tc = new TabContent();
        tabPanel.addTab(ci.getLabel(), tc.initTabContent(wic));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.initLoginDialog();
        }
    }
}
