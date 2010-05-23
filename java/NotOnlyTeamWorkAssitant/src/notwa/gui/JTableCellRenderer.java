/*
 * JTableCellRenderer
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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import notwa.common.ColorManager;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class JTableCellRenderer implements TableCellRenderer
{
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    private WorkItemCollection wic;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        final Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent
                                    (table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            renderer.setBackground(ColorManager.getTableSelectedRowColor());
        }
        else {
            if (row % 2 == 0) {
                renderer.setBackground(ColorManager.getTableEvenRowColor());

            } else {
                renderer.setBackground(ColorManager.getTableOddRowColor());
            }

            /*
             * Colorize Priority and status cells with colors loaded from ColorManager class
             */
            if ((value != null) && (value instanceof WorkItemPriority)) {
                WorkItemPriority wip = (WorkItemPriority) value;
                renderer.setBackground(ColorManager.getPriorityColor(wip));
            } else if ((value != null) && (value instanceof WorkItemStatus)) {
                WorkItemStatus wis = (WorkItemStatus) value;
                renderer.setBackground(ColorManager.getStatusColor(wis));
            }
        }
        
        return renderer;
    }
    
    public void setWorkItemCollection(WorkItemCollection wic) {
        this.wic = wic;
    }
}
