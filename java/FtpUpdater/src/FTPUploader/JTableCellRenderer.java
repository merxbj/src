package FTPUploader;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


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
	    		renderer.setBackground(Color.LIGHT_GRAY);
	    		
	    	}
	    	else
	    	{
	    		renderer.setBackground(Color.WHITE);
	    	}
		}
		
		if(row < FTPUploader.customers.size())
		{
			if (value.equals(FTPUploader.customers.get(row)[0]))
			{
				if(FTPUploader.customers.get(row)[5].equals("D"))
				{
					renderer.setBackground(Color.GREEN);
				}
				else if(FTPUploader.customers.get(row)[5].equals("E"))
				{
					renderer.setBackground(Color.RED);
				}
			}			
		}
	    return renderer;
	}
}