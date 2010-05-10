/*
 * WorkItemNoteHistoryTable
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

import java.awt.GridLayout;
import javax.swing.JComponent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import notwa.gui.datamodels.NoteHistoryModel;

import notwa.wom.NoteCollection;
import notwa.wom.WorkItem;

public class WorkItemNoteHistoryTable extends JComponent {
    private NoteHistoryModel nhTableModel;
    private JTable nhTable;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    
    private NoteCollection noteCollection;

    public WorkItemNoteHistoryTable() {
        init();
    }

    public void init() {
        this.setLayout(new GridLayout(1,0));

        nhTableModel = new NoteHistoryModel(noteCollection);
        nhTable = new JTable(nhTableModel);
        nhTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.resizeAndColorizeTable();

        this.add(new JScrollPane(nhTable));
    }
    
    private void resizeAndColorizeTable() {
        nhTable.getColumnModel().getColumn(0).setMaxWidth(100);
        nhTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        nhTable.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
    }
    
    public void setNoteCollection(NoteCollection nc) {
        try {
            nhTable.setModel(new NoteHistoryModel(nc));
            this.resizeAndColorizeTable();
        } catch (Exception e) {} 
    }

    public void setAllToNull() {
        this.setNoteCollection(null);
    }

    public void loadFromWorkItem(WorkItem wi) {
        if (wi != null)
            setNoteCollection(wi.getNoteCollection());
    }
}