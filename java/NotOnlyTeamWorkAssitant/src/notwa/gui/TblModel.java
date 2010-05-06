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
        Object record = getRecord(rowIndex);
        
        if (tableHeader[columnIndex].equals("Product")) {
            return ((WorkItem)record).getProject().getName();
        }
        else if (tableHeader[columnIndex].equals("WIT ID")) {
            return ((WorkItem)record).getId();
        }
        else if (tableHeader[columnIndex].equals("Subject")) {
            return ((WorkItem)record).getSubject();
        }
        else if (tableHeader[columnIndex].equals("Priority")) {
            WorkItem wi = (WorkItem)record;
            try {
                return new JAnyItemCreator(wi.getPriority(), wi.getPriority().toString());
            } catch (Exception e) {
                return new JAnyItemCreator(WorkItemPriority.NICE_TO_HAVE, WorkItemPriority.NICE_TO_HAVE.toString());
            }
        }
        else if (tableHeader[columnIndex].equals("Assigned")) {
            return ((WorkItem)record).getAssignedUser().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Status")) {
            WorkItem wi = (WorkItem)record;
            try {
                return new JAnyItemCreator(wi.getStatus(), wi.getStatus().toString());
            } catch (Exception e) {
                return new JAnyItemCreator(WorkItemStatus.RESOLVE, WorkItemStatus.RESOLVE.toString());
            }
        }
        else if (tableHeader[columnIndex].equals("Note author")) {
            return ((Note)record).getAuthor().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Text")) {
            return ((Note)record).getText();
        }
        else if (tableHeader[columnIndex].equals("Login")) {
            User user = (User)record;
            return new JAnyItemCreator(user, user.getLogin());
        }
        else if (tableHeader[columnIndex].equals("Name")) {
            return ((User)record).getFirstName();
        }
        else if (tableHeader[columnIndex].equals("Last name")) {
            return ((User)record).getLastName();
        }
        else {
            return record;
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
