/*
 * TblModel
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

import javax.swing.table.AbstractTableModel;

import notwa.wom.BusinessObjectCollection;
import notwa.wom.Note;
import notwa.wom.User;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class TblModel extends AbstractTableModel {
    private BusinessObjectCollection<?> boc;
    private String[] tableHeader;
   
    public TblModel(BusinessObjectCollection<?> boc, String[] tableHeader) {
        this.boc = boc;
        this.tableHeader = tableHeader;
    }

    @Override
    public String getColumnName(int c) {
        return tableHeader[c];
    }

    @Override
    public int getColumnCount() {
        return tableHeader.length;
    }

    @Override
    public int getRowCount() {
        return (boc == null) ? 0 : boc.size();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // nothing is editable
        super.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (tableHeader[columnIndex].equals("Product")) {
            return ((WorkItem)getRecord(rowIndex)).getProject().getName();
        }
        else if (tableHeader[columnIndex].equals("WIT ID")) {
            return ((WorkItem)getRecord(rowIndex)).getId();
        }
        else if (tableHeader[columnIndex].equals("Subject")) {
            return ((WorkItem)getRecord(rowIndex)).getSubject();
        }
        else if (tableHeader[columnIndex].equals("Priority")) {
            try {
                return ((WorkItem)getRecord(rowIndex)).getPriority().toString();
            } catch (Exception e) {
                return WorkItemPriority.NICE_TO_HAVE.toString();
            }
        }
        else if (tableHeader[columnIndex].equals("Assigned")) {
            return ((WorkItem)getRecord(rowIndex)).getAssignedUser().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Status")) {
            try {
                return ((WorkItem)getRecord(rowIndex)).getStatus().toString();
            } catch (Exception e) {
                return WorkItemStatus.RESOLVE.toString();
            }
        }
        else if (tableHeader[columnIndex].equals("Note author")) {
            return ((Note)getRecord(rowIndex)).getAuthor().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Text")) {
            return ((Note)getRecord(rowIndex)).getText();
        }
        else if (tableHeader[columnIndex].equals("Login")) {
            User user = (User)getRecord(rowIndex);
            return new JListItemCreator(user, user.getLogin());
        }
        else if (tableHeader[columnIndex].equals("Name")) {
            return ((User)getRecord(rowIndex)).getFirstName();
        }
        else if (tableHeader[columnIndex].equals("Last name")) {
            return ((User)getRecord(rowIndex)).getLastName();
        }
        else {
            return getRecord(rowIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ((boc == null) || (boc.size() == 0)) {
            return Object.class;
        }
        Object o = getValueAt(0, columnIndex);
        return (o == null) ? Object.class : o.getClass();
    }
    
    private Object getRecord(int rowIndex) {
        return boc.get(rowIndex);
    }
}
