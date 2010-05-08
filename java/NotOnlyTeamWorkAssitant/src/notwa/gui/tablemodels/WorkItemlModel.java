/*
 * WorkItemlModel
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

import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

/**
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WorkItemlModel extends AbstractTableModel {

    private WorkItemCollection data;
    private Hashtable<Integer, ColumnSettings<WorkItemTableColumn>> columns;

    public WorkItemlModel(WorkItemCollection data) {
        this.data = data;
        configureColumns();
    }

    @Override
    public String getColumnName(int c) {
        ColumnSettings<WorkItemTableColumn> cs = columns.get(c);
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
            WorkItem row = data.get(rowIndex);
            ColumnSettings<WorkItemTableColumn> cs = columns.get(columnIndex);

            switch (cs.getColumnAlias()) {
                case COLUMN_PROJECT_NAME_ALIAS:
                    return (row.getProject() != null) ? row.getProject().getName() : null;
                case COLUMN_WORK_ITEM_ID_ALIAS:
                    return row.getId();
                case COLUMN_WORK_ITEM_SUBJECT_ALIAS:
                    return row.getSubject();
                case COLUMN_WORK_ITEM_PRIORITY_ALIAS:
                    return row.getPriority();
                case COLUMN_ASSIGNED_USER_LOGIN_ALIAS:
                    return (row.getAssignedUser() != null) ? row.getAssignedUser().getLogin() : null;
                case COLUMN_WORK_ITEM_STATUS_ALIAS:
                    return row.getStatus();
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
        ColumnSettings<WorkItemTableColumn> cs = columns.get(columnIndex);
        return (cs != null) ? cs.getClass() : Object.class;
    }

    public ColumnSettings<WorkItemTableColumn> getColumnSettings(int columnIndex) {
        if (columnIndex < columns.size()) {
            return columns.get(columnIndex);
        } else {
            return null;
        }
    }

    private void configureColumns() {
        columns = new Hashtable<Integer, ColumnSettings<WorkItemTableColumn>>(6);
        columns.put(0, new ColumnSettings<WorkItemTableColumn>(0, String.class,           "Project",     WorkItemTableColumn.COLUMN_PROJECT_NAME_ALIAS));
        columns.put(1, new ColumnSettings<WorkItemTableColumn>(1, Integer.class,          "WIT#",        WorkItemTableColumn.COLUMN_WORK_ITEM_ID_ALIAS));
        columns.put(2, new ColumnSettings<WorkItemTableColumn>(2, String.class,           "Subject",     WorkItemTableColumn.COLUMN_WORK_ITEM_SUBJECT_ALIAS));
        columns.put(3, new ColumnSettings<WorkItemTableColumn>(3, WorkItemPriority.class, "Priority",    WorkItemTableColumn.COLUMN_WORK_ITEM_PRIORITY_ALIAS));
        columns.put(4, new ColumnSettings<WorkItemTableColumn>(4, String.class,           "Assigned To", WorkItemTableColumn.COLUMN_ASSIGNED_USER_LOGIN_ALIAS));
        columns.put(5, new ColumnSettings<WorkItemTableColumn>(5, WorkItemStatus.class,   "Status",      WorkItemTableColumn.COLUMN_WORK_ITEM_STATUS_ALIAS));
    }

    public enum WorkItemTableColumn {
        COLUMN_PROJECT_NAME_ALIAS,
        COLUMN_WORK_ITEM_ID_ALIAS,
        COLUMN_WORK_ITEM_SUBJECT_ALIAS,
        COLUMN_WORK_ITEM_PRIORITY_ALIAS,
        COLUMN_ASSIGNED_USER_LOGIN_ALIAS,
        COLUMN_WORK_ITEM_STATUS_ALIAS;
    }
}
