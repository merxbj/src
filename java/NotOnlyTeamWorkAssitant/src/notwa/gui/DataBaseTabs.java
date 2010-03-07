package notwa.gui;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class DataBaseTabs extends JTabbedPane {

	public DataBaseTabs () {
		//TODO: must be loaded from config - lastly used tabs(databases)
		JComponent defaultPanel = new TabsContent();
		this.addTab("Default", defaultPanel);
		
		JComponent plusPanel = null;
		this.addTab("+", plusPanel); //TODO: must be like button, on click it will call window to create new tab
	}
}
