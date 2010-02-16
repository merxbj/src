package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JStatusBar extends JPanel
{
	static JPanel rightPanel;

	public JStatusBar()
	{
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 20));

		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setOpaque(false);

		add(rightPanel, BorderLayout.EAST);
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		int y = 0;
		g.setColor(new Color(156, 154, 140));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(196, 194, 183));
		g.drawLine(0, y, getWidth(), y);
	}
}