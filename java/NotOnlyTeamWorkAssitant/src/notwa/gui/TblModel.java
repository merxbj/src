package notwa.gui;

import javax.swing.table.AbstractTableModel;

import notwa.wom.BusinessObjectCollection;
import notwa.wom.Note;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

class TblModel extends AbstractTableModel {
    private BusinessObjectCollection<?> boc;
    private String[] tableHeader;
   
    public TblModel(BusinessObjectCollection<?> boc, String[] tableHeader) {
        this.boc = boc;
        this.tableHeader = tableHeader;
    }

    public String getColumnName(int c) {
        return tableHeader[c];
    }

    public int getColumnCount() {
        return tableHeader.length;
    }

    public int getRowCount() {
        return boc == null ? 0 : boc.size();
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // nothing is editable
        super.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
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
                return WorkItemPriority.UNNECESSARY.name();
            }
        }
        else if (tableHeader[columnIndex].equals("Assigned")) {
            return ((WorkItem)getRecord(rowIndex)).getAssignedUser().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Status")) {
            try {
                return ((WorkItem)getRecord(rowIndex)).getStatus().toString();
            } catch (Exception e) {
                return WorkItemStatus.PLEASE_RESOLVE.name();
            }
        }
        else if (tableHeader[columnIndex].equals("Note author")) {
            return ((Note)getRecord(rowIndex)).getAuthor().getLogin();
        }
        else if (tableHeader[columnIndex].equals("Text")) {
            return ((Note)getRecord(rowIndex)).getText();
        }
        else {
            return getRecord(rowIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (boc == null || boc.size() == 0) {
            return Object.class;
        }
        Object o = getValueAt(0, columnIndex);
        return o == null ? Object.class : o.getClass();
    }
    
    private Object getRecord(int rowIndex) {
        return boc.get(rowIndex);
    }
}
