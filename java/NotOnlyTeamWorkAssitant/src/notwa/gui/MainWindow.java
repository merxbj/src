package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	private String version = new String("v0.0.1-r1"); //config?
	
	public MainWindow() {
		initMainWindow();
	}

	private void initMainWindow() {
		this.setLayout(new BorderLayout());
		this.setTitle("NOTWA - NOT Only Team Work Assistent " + version.toString());
		this.setSize(1000,500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*
		 * Load MainMenu
		 */
		MainMenu mm = new MainMenu();
		this.setJMenuBar(mm);
		
		/*
		 * create main JPanel and Load tabs 
		 */
		JPanel mainJP = new JPanel(new BorderLayout());
		DataBaseTabs dbt = new DataBaseTabs();
		mainJP.add(dbt, BorderLayout.CENTER);
		this.add(mainJP,BorderLayout.CENTER);
		
		/*
		 * create JStatusBar object
		 */
		JStatusBar jsb = new JStatusBar();
		this.add(jsb, BorderLayout.PAGE_END);
		
		this.setVisible(true);
	}
}
