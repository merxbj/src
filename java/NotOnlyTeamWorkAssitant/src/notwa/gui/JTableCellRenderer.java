package notwa.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

//TODO: rewrite it to our use
//    : load colors from config ... so on

public class JTableCellRenderer implements TableCellRenderer
{
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        final Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent
                                    (table, value, isSelected, hasFocus, row, column);
        
        if (isSelected) {
            renderer.setBackground(new Color(150, 230, 230));
        } else {
            if (row % 2 == 0) {
                renderer.setBackground(new Color(240, 240, 240));

            } else {
                renderer.setBackground(Color.WHITE);
            }
        }
        return renderer;
    }
}
