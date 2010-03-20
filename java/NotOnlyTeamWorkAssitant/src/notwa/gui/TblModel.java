package notwa.gui;

import javax.swing.table.AbstractTableModel;

import notwa.wom.ContextManager;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

class TblModel extends AbstractTableModel {
    private static WorkItemCollection wic;
    private String[] tableHeader;

    public TblModel(WorkItemCollection wic, String[] tableHeader) {
        this.wic = wic;
        this.tableHeader = tableHeader;
    }

    public String getColumnName(int c) {
        return tableHeader[c];
    }

    public int getColumnCount() {
        return tableHeader.length;
    }

    public int getRowCount() {
        return wic == null ? 0 : wic.size();
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // getRecord(rowIndex)[columnIndex] = value; // TODO add setter
        super.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (tableHeader[columnIndex].equals("Product")) {
            return getRecord(rowIndex).getProject().getName();
        }
        else if (tableHeader[columnIndex].equals("WIT ID")) {
            return getRecord(rowIndex).getId();
        }
        else if (tableHeader[columnIndex].equals("Subject")) {
            return getRecord(rowIndex).getSubject();
        }
        else if (tableHeader[columnIndex].equals("Priority")) {
            try {
                return getRecord(rowIndex).getPriority().toString();
            } catch (Exception e) {
                return WorkItemPriority.UNNECESSARY.name();
            }
        }
        else if (tableHeader[columnIndex].equals("Assigned")) {
            return getRecord(rowIndex).getAssignedUser().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Status")) {
            try {
                return getRecord(rowIndex).getStatus().toString();
            } catch (Exception e) {
                return WorkItemStatus.PLEASE_RESOLVE.name();
            }
        }
        else {
            return getRecord(rowIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // TODO: can be edited only by owner of wit and so on ...
        if (columnIndex < 3) // this will be more accurate
            return false;
        return true;
    }

    public Class getColumnClass(int columnIndex) {
        if (wic == null || wic.size() == 0) {
            return Object.class;
        }
        Object o = getValueAt(0, columnIndex);
        return o == null ? Object.class : o.getClass();
    }
    
    private WorkItem getRecord(int rowIndex) {
        return (WorkItem) wic.get(rowIndex);
    }
}
