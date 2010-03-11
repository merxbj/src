package notwa.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class MainMenu extends JMenuBar implements ActionListener {
	private JMenu menu;
	private JMenuItem mItemSyncAndRefresh,mItemExit,mItemConfigure;
	private JCheckBoxMenuItem cbWorkOffline;
	private JTextField searchField = new JTextField("Type here ...");
	
	public MainMenu() {
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		this.add(menu);

		mItemSyncAndRefresh = new JMenuItem("Synchronize & Refresh", KeyEvent.VK_F5);
		mItemSyncAndRefresh.setAccelerator(KeyStroke.getKeyStroke("F5"));
		menu.add(mItemSyncAndRefresh);

		menu.addSeparator();
		cbWorkOffline = new JCheckBoxMenuItem("Work offline");
		cbWorkOffline.setMnemonic(KeyEvent.VK_O);
		menu.add(cbWorkOffline);

		menu.addSeparator();
		mItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		mItemExit.addActionListener(this);
		menu.add(mItemExit);

		/*
		 * Build settings menu bar.
		 */
		menu = new JMenu("Settings");
		menu.setMnemonic(KeyEvent.VK_S);
		this.add(menu);

		mItemConfigure = new JMenuItem("Configure", KeyEvent.VK_C);
		mItemConfigure.addActionListener(this);
		menu.add(mItemConfigure);
		
		/*
		 * Add search panel to MainMenu
		 */
		this.add(Box.createHorizontalGlue());

		searchField.setMaximumSize(new Dimension(2500,20));
		this.add(new JLabel("| Search "));
		this.add(searchField);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		//TODO: add remaining actions
		if (ae.getSource() == mItemConfigure) {
			SettingsDialog sd = new SettingsDialog();
		}
		
		if (ae.getSource() == mItemExit)	{
			System.exit(-1);
		}
	}
}
