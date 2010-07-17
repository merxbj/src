/*
 * JPasswordFieldRenderer
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
package notwa.gui.components;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import notwa.common.ColorManager;

public class JPasswordFieldRenderer extends JPasswordField implements TableCellRenderer
{
    public JPasswordFieldRenderer() {
        super();
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            this.setBackground(ColorManager.getTableSelectedRowColor());
        }
        else {
            if (row % 2 == 0) {
                this.setBackground(ColorManager.getTableEvenRowColor());

            } else {
                this.setBackground(ColorManager.getTableOddRowColor());
            }
        }
        setFont(table.getFont());
        setValue(value);
        return this;
    }
    
    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }
    
    @Override
    public boolean isEditable() {
        return true;
    }
}