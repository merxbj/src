package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TabsContent extends JComponent {
	
	//TODO: must have parameter to know what information we want to show
	public TabsContent() {
		this.setLayout(new GridLayout(2,0));
		
		JPanel witJPanel = new WITJPanel();
		this.add(witJPanel);
		
		JPanel witDetailJPanel = new WITDetailPanel();
		this.add(witDetailJPanel);
	}
}
