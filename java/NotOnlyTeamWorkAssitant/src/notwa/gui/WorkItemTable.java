package notwa.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

@SuppressWarnings("serial")
public class WorkItemTable extends JPanel{
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    
    public WorkItemTable(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        
        fillWits(); //temporary
        
        String[] tableHeaders = {"Product", "WIT ID", "Subject", "Priority", "Assigned", "Status"};
        
        //TODO: change tblModel to accept WorkItemCollection
        TblModel witTableModel = new TblModel(data, tableHeaders);
        JTable witTable = new JTable(witTableModel);

        /*
         * Sets columns min/max width and create new JTableCellRender for table coloring
         */
        JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
        for (int c = 0; c < tableHeaders.length; c++) {
            if(!tableHeaders[c].equals("Subject")) {
            witTable.getColumnModel().getColumn(c).setMinWidth(100);
            witTable.getColumnModel().getColumn(c).setMaxWidth(100);
            }
            witTable.getColumnModel().getColumn(c).setCellRenderer(tableCellRenderer);
        }

        /*
         * load all WorkItemPriorits from enum
         */
        JComboBox priority = new JComboBox();
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priority.addItem(WorkItemPriority.values()[p]);
        }
        witTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(priority));
        
        /*
         * TODO: will be loaded when constructing all existing users
         */
        JComboBox assignedUsers = new JComboBox();
        assignedUsers.addItem("mrneo");
        assignedUsers.addItem("eter");
        witTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(assignedUsers));
        
        /*
         * load all WorkItemStates from enum
         */
        JComboBox status = new JComboBox();
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            status.addItem(WorkItemStatus.values()[s].name());
        }
        witTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(status));
        
        JScrollPane jsp = new JScrollPane(witTable);
        
        this.add(jsp, BorderLayout.CENTER);
    }
    
    private void fillWits() {
        data.add(new Object[]{"notwa", "XX-00001", "Do this class", "CRITICAL", "mrneo", "IN_PROGRESS"});
        data.add(new Object[]{"notwa", "XX-00002", "Do another class", "CRITICAL", "mrneo", "IN_PROGRESS"});
        data.add(new Object[]{"notwa", "XX-00003", "Do b class", "CRITICAL", "mrneo", "IN_PROGRESS"});
        data.add(new Object[]{"notwa", "XX-00004", "Do c class", "CRITICAL", "mrneo", "IN_PROGRESS"});
    }
}
