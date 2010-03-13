package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class LoadMainLayout extends JPanel{
	private JTabbedPane tabPanel;
	
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
		JComponent defaultPanel = new TabContent();
		tabPanel.addTab("Default", defaultPanel);
		
		tabPanel.addTab(null,null); //create empty tab, where we will add new button

		JButton plusButton = new JButton("+");
		plusButton.setBorder(BorderFactory.createEtchedBorder());
		plusButton.setPreferredSize(new Dimension(30,20));
		tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, plusButton);
		//TODO: add action listener!
		return tabPanel;		
	}
}
