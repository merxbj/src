/*
 * NoteHistoryModel
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
package notwa.gui.tablemodels;

import java.util.Hashtable;
import javax.swing.table.AbstractTableModel;
import notwa.logger.LoggingFacade;
import notwa.wom.Note;
import notwa.wom.NoteCollection;

/**
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class NoteHistoryModel extends AbstractTableModel {

    private NoteCollection data;
    private Hashtable<Integer, ColumnSettings<NoteHistoryTableColumn>> columns;

    public NoteHistoryModel(NoteCollection data) {
        this.data = data;
        configureColumns();
    }

    @Override
    public String getColumnName(int c) {
        ColumnSettings<NoteHistoryTableColumn> cs = columns.get(c);
        return (cs != null) ? cs.getColumnHeader() : "Hey! I need name!";
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // nothing is editable
        super.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        try {
            Note row = data.get(rowIndex);
            ColumnSettings<NoteHistoryTableColumn> cs = columns.get(columnIndex);

            switch (cs.getColumnAlias()) {
                case COLUMN_NOTE_AUTHOR_ALIAS:
                    return (row.getAuthor() != null) ? row.getAuthor().getLogin() : "unknown";
                case COLUMN_NOTE_TEXT_ALIAS:
                    return row.getText();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        ColumnSettings<NoteHistoryTableColumn> cs = columns.get(columnIndex);
        return (cs != null) ? cs.getClass() : Object.class;
    }

    public ColumnSettings<NoteHistoryTableColumn> getColumnSettings(int columnIndex) {
        if (columnIndex < columns.size()) {
            return columns.get(columnIndex);
        } else {
            return null;
        }
    }

    private void configureColumns() {
        columns = new Hashtable<Integer, ColumnSettings<NoteHistoryTableColumn>>(2);
        columns.put(0, new ColumnSettings<NoteHistoryTableColumn>(0, String.class, "Author", NoteHistoryTableColumn.COLUMN_NOTE_AUTHOR_ALIAS));
        columns.put(1, new ColumnSettings<NoteHistoryTableColumn>(1, String.class, "Text",   NoteHistoryTableColumn.COLUMN_NOTE_TEXT_ALIAS));
    }

    public enum NoteHistoryTableColumn {
        COLUMN_NOTE_AUTHOR_ALIAS,
        COLUMN_NOTE_TEXT_ALIAS;
    }
}
