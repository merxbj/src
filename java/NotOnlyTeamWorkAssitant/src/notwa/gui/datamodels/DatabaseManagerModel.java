/*
 * DatabaseManagerModel
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
package notwa.gui.datamodels;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import notwa.common.NotwaConnectionInfo;
import notwa.logger.LoggingFacade;

public class DatabaseManagerModel extends DefaultTableModel {

    private Collection<NotwaConnectionInfo> data;
    private Hashtable<Integer, ColumnSettings<DatabaseManagerTableColumn>> columns;

    public DatabaseManagerModel(Collection<NotwaConnectionInfo> nci) {
        this.data = nci;
        configureColumns();
    }

    @Override
    public String getColumnName(int c) {
        ColumnSettings<DatabaseManagerTableColumn> cs = columns.get(c);
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
    public void addRow(Vector v) {
        NotwaConnectionInfo nci = new NotwaConnectionInfo();
        nci.setLabel("New " + (this.getRowCount()+1));
        data.add(nci);
        super.fireTableDataChanged();
    }
    
    @Override
    public void removeRow(int row) {
        data.remove(data.toArray()[row]);
        super.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        try {
            NotwaConnectionInfo row = (NotwaConnectionInfo)data.toArray()[rowIndex];
            ColumnSettings<DatabaseManagerTableColumn> cs = columns.get(columnIndex);
            switch (cs.getColumnAlias()) {
                case COLUMN_DATABASE_LABEL:
                    row.setLabel((String)value);
                    break;
                case COLUMN_DATABASE_DBNAME:
                    row.setDbname((String)value);
                    break;
                case COLUMN_DATABASE_HOST:
                    row.setHost((String)value);
                    break;
                case COLUMN_DATABASE_PORT:
                    row.setPort((String)value);
                    break;
                case COLUMN_DATABASE_USER:
                    row.setUser((String)value);
                    break;
                case COLUMN_DATABASE_PASSWORD:
                    row.setPassword((String)value);
                    break;
                default:
                    throw new Exception("Unsupported cell update!");
            }
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
        super.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        try {
            NotwaConnectionInfo row = (NotwaConnectionInfo)data.toArray()[rowIndex];
            ColumnSettings<DatabaseManagerTableColumn> cs = columns.get(columnIndex);

            switch (cs.getColumnAlias()) {
                case COLUMN_DATABASE_LABEL:
                    return row.getLabel();
                case COLUMN_DATABASE_DBNAME:
                    return row.getDbname();
                case COLUMN_DATABASE_HOST:
                    return row.getHost();
                case COLUMN_DATABASE_PORT:
                    return row.getPort();
                case COLUMN_DATABASE_USER:
                    return row.getUser();
                case COLUMN_DATABASE_PASSWORD:
                    return "|_PASSWORD_PROTECTED_|";
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
        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        ColumnSettings<DatabaseManagerTableColumn> cs = columns.get(columnIndex);
        return (cs != null) ? cs.getClass() : Object.class;
    }

    public ColumnSettings<DatabaseManagerTableColumn> getColumnSettings(int columnIndex) {
        if (columnIndex < columns.size()) {
            return columns.get(columnIndex);
        } else {
            return null;
        }
    }

    private void configureColumns() {
        columns = new Hashtable<Integer, ColumnSettings<DatabaseManagerTableColumn>>(1);
        columns.put(0, new ColumnSettings<DatabaseManagerTableColumn>(0, String.class, "Label", DatabaseManagerTableColumn.COLUMN_DATABASE_LABEL));
        columns.put(1, new ColumnSettings<DatabaseManagerTableColumn>(1, String.class, "Database name", DatabaseManagerTableColumn.COLUMN_DATABASE_DBNAME));
        columns.put(2, new ColumnSettings<DatabaseManagerTableColumn>(2, String.class, "Host", DatabaseManagerTableColumn.COLUMN_DATABASE_HOST));
        columns.put(3, new ColumnSettings<DatabaseManagerTableColumn>(3, String.class, ":Port", DatabaseManagerTableColumn.COLUMN_DATABASE_PORT));
        columns.put(4, new ColumnSettings<DatabaseManagerTableColumn>(4, String.class, "DB User", DatabaseManagerTableColumn.COLUMN_DATABASE_USER));
        columns.put(5, new ColumnSettings<DatabaseManagerTableColumn>(5, String.class, "DB Password", DatabaseManagerTableColumn.COLUMN_DATABASE_PASSWORD));
    }

    public enum DatabaseManagerTableColumn {
        COLUMN_DATABASE_LABEL, COLUMN_DATABASE_DBNAME, COLUMN_DATABASE_HOST,
        COLUMN_DATABASE_PORT, COLUMN_DATABASE_USER, COLUMN_DATABASE_PASSWORD
    }
}