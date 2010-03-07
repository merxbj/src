package notwa.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow {

	private JFrame mainWindow;
	private String version = new String("v0.0.1-r1"); //config?
	
	public MainWindow() {
		initMainWindow();
	}

	private void initMainWindow() {
		mainWindow = new JFrame("NOTWA - NOT Only Team Work Assistent " + version.toString());
		mainWindow.setPreferredSize(new Dimension(1000,500));
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}
}
