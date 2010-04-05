/*
 * JTableCellRender
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
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

    @Override
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
