package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WITJPanel extends JPanel {
	JButton addButton,editButton,showHideButton,showDepButton;
	
	public WITJPanel() {
		this.setLayout(new BorderLayout());
		
		JPanel witTable = new WITTable();
		this.add(witTable, BorderLayout.CENTER);
		
		JPanel ib = initButtons();
		this.add(ib, BorderLayout.PAGE_END);
	}
	
	private JPanel initButtons() {
		JPanel buttonsPanel = new JPanel(new BorderLayout());
		
		JPanel leftButtonsPanel = new JPanel();
		JPanel rightButtonsPanel = new JPanel();
		
		addButton = new JButton("Add");
		editButton = new JButton("Edit");
		showHideButton = new JButton("Show/Hide Detail");
		showDepButton = new JButton("Show dependency tree");
		
		//TODO: actions
		
		leftButtonsPanel.add(addButton);
		leftButtonsPanel.add(editButton);
		rightButtonsPanel.add(showHideButton);
		rightButtonsPanel.add(showDepButton);
		
		buttonsPanel.add(leftButtonsPanel, BorderLayout.LINE_START);
		buttonsPanel.add(rightButtonsPanel, BorderLayout.LINE_END);
		
		return buttonsPanel;
	}
}
