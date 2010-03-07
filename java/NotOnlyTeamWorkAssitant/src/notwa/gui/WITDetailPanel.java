package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WITDetailPanel extends JPanel {
	public WITDetailPanel() {
		this.setLayout(new GridLayout(3,0));
		
		this.add(new JLabel("aditional informations"));
		this.add(new JLabel("more informations"));
		this.add(new JLabel("mooooore informations"));
	}
}
