/*
 * JTextAreaRenderer
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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import notwa.common.ColorManager;

public class JTextAreaRenderer extends JTextArea implements TableCellRenderer {
    private List<List<Integer>> rowColHeight = new ArrayList<List<Integer>>();
    
    public JTextAreaRenderer() {
        super();
        setLineWrap(true);
        setWrapStyleWord(true);
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
        adjustRowHeight(table, row, column);
        return this;
    }
    
    private void adjustRowHeight(JTable table, int row, int column) {
        int columnWidth = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
        setSize(new Dimension(columnWidth, 1000));

        while (rowColHeight.size() <= row) {
          rowColHeight.add(new ArrayList<Integer>(column));
        }
        
        List<Integer> colHeights = rowColHeight.get(row);
        while (colHeights.size() <= column) {
          colHeights.add(0);
        }
        
        int maxHeight = getPreferredSize().height;
        colHeights.set(column, maxHeight);
        for (Integer colHeight : colHeights) {
          if (colHeight > maxHeight) {
            maxHeight = colHeight;
          }
        }
        
        if (table.getRowHeight(row) != maxHeight) {
          table.setRowHeight(row, maxHeight);
        }
    }
    
    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }
}