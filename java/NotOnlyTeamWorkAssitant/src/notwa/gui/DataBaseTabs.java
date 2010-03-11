package notwa.gui;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class DataBaseTabs extends JTabbedPane{

	public DataBaseTabs () {
	    this.setOpaque(false);
		//TODO: must be loaded from config - lastly used tabs(databases)
		JComponent defaultPanel = new TabsContent();
		this.addTab("Default", defaultPanel);
		
		JComponent plusPanel = null;
		this.addTab("+", plusPanel); //TODO: must be like button, on click it will call window to create new tab
		//TODO: ADD plusPanel.addMouseListener()
		//TODO: add 2x comboboxes for filtering
		
		//this.setTabPlacement(JTabbedPane.LEFT);
	}
}
