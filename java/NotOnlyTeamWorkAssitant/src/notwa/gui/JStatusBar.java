package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JStatusBar extends JPanel {
	public JStatusBar() {
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Synchronizing with repository ..."), BorderLayout.LINE_START);
	}

}
