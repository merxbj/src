/*
 * UserManagementModel
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
import notwa.wom.Project;
import notwa.wom.ProjectCollection;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ProjectManagementModel extends AbstractTableModel {

    private ProjectCollection data;
    private Hashtable<Integer, ColumnSettings<ProjectManagementTableColumn>> columns;

    public ProjectManagementModel(ProjectCollection data) {
        this.data = data;
        configureColumns();
    }

    @Override
    public String getColumnName(int c) {
        ColumnSettings<ProjectManagementTableColumn> cs = columns.get(c);
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
            Project row = data.get(rowIndex);
            ColumnSettings<ProjectManagementTableColumn> cs = columns.get(columnIndex);

            switch (cs.getColumnAlias()) {
                case COLUMN_PROJECT_NAME_ALIAS:
                    return row.getName();
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
        ColumnSettings<ProjectManagementTableColumn> cs = columns.get(columnIndex);
        return (cs != null) ? cs.getClass() : Object.class;
    }

    public ColumnSettings<ProjectManagementTableColumn> getColumnSettings(int columnIndex) {
        if (columnIndex < columns.size()) {
            return columns.get(columnIndex);
        } else {
            return null;
        }
    }

    private void configureColumns() {
        columns = new Hashtable<Integer, ColumnSettings<ProjectManagementTableColumn>>(1);
        columns.put(0, new ColumnSettings<ProjectManagementTableColumn>(0, String.class, "Project name", ProjectManagementTableColumn.COLUMN_PROJECT_NAME_ALIAS));
    }

    public enum ProjectManagementTableColumn {
        COLUMN_PROJECT_NAME_ALIAS,
    }
}
