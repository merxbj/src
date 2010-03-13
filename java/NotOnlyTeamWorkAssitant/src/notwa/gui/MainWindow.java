package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	private String version = new String("v0.0.1-r1"); //config?
	
	public MainWindow() {
		
		/*
		 * temp this will be removed when loading from config is completly done
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * end.
		 */
		
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
		LoadMainLayout dbt = new LoadMainLayout();
		mainJP.add(dbt.initMainLayout(), BorderLayout.CENTER);
		this.add(mainJP,BorderLayout.CENTER);
		
		/*
		 * create JStatusBar object
		 */
		JStatusBar jsb = new JStatusBar();
		this.add(jsb, BorderLayout.PAGE_END);
		
		this.setVisible(true);
	}
}
