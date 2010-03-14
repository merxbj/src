package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class LoadMainLayout extends JPanel implements ActionListener {
    private JTabbedPane tabPanel;
    private JButton plusButton;
    
    public LoadMainLayout () {
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
        tabPanel.addTab("Default", tc.initTabContent());
        
        tabPanel.addTab(null,null); //create empty tab, where we will add new button

        plusButton = new JButton("+");
        plusButton.setBorder(BorderFactory.createEtchedBorder());
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);
        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, plusButton);

        return tabPanel;        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.initLoginDialog();
        }
    }
}
