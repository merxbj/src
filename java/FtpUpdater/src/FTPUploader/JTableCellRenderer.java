package FTPSynchronizer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import FTPSynchronizer.FTPSyncMainWindow.customersArrayMap;


public class JTableCellRenderer implements TableCellRenderer
{
	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	public Component getTableCellRendererComponent(final JTable table, final Object value,
			final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		final Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (isSelected)
		{
			Color selected = new Color(150,230,230);

			renderer.setBackground(selected);
		}
		else
		{
	    	if (row % 2 == 0)
	    	{
	    		Color secondColor = new Color(240,240,240);
	    		
	    		renderer.setBackground(secondColor);
	    		
	    	}
	    	else
	    	{
	    		renderer.setBackground(Color.WHITE);
	    	}
		}
		
		if(row < FTPSyncMainWindow.choosenCustomers.size())
		{
			if (value.equals(FTPSyncMainWindow.choosenCustomers.get(row)[0]))
			{
				if(FTPSyncMainWindow.choosenCustomers.get(row)[customersArrayMap.UPLOADSTATUS.ordinal()].equals("D"))
				{
					renderer.setBackground(Color.GREEN);
				}
				else if(FTPSyncMainWindow.choosenCustomers.get(row)[customersArrayMap.UPLOADSTATUS.ordinal()].equals("E"))
				{
					renderer.setBackground(Color.RED);
				}
			}			
		}
	    return renderer;
	}
}