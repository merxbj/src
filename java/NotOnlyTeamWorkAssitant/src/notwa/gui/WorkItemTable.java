package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

public class WorkItemTable extends TabContent{
    private String[] tableHeaders = {
            "Product", "WIT ID", "Subject", "Priority", "Assigned", "Status", "WI"};
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private static JTable witTable;
    private TblModel witTableModel;
    public static TableRowSorter<TblModel> sorter;

    public WorkItemTable(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        
        witTableModel = new TblModel(wic, tableHeaders);
        witTable = new JTable();
        witTable.setModel(witTableModel);
        witTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<TblModel>(witTableModel);
        witTable.setRowSorter(sorter);
        
        witTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                WorkItemDetail.getInstance().fillWithActualData();
            }
        });
        
        this.resizeAndColorizeTable();

        witTable.getColumnModel()
                .getColumn(3)
                .setCellEditor( new DefaultCellEditor(
                                this.loadWorkItemPriorties()));
        
        witTable.getColumnModel()
                .getColumn(4)
                .setCellEditor( new DefaultCellEditor(
                                this.loadProjectUsers()));
                
        witTable.getColumnModel()
                .getColumn(5)
                .setCellEditor( new DefaultCellEditor(
                                this.loadWorkItemStates()));
        /*
         * Hide last column containing whole WorkItem for WorkItemDetail
         */
        witTable.getColumnModel().removeColumn(witTable.getColumnModel().getColumn(6));
        
        JScrollPane jsp = new JScrollPane(witTable);
        
        this.add(jsp, BorderLayout.CENTER);
    }
    
    private JComboBox loadWorkItemStates() {
        // TODO change the way of adding items to JComboBoxItemCreator
        JComboBox status = new JComboBox();
        for (int s = 0; s < WorkItemStatus.values().length; s++) {
            status.addItem(WorkItemStatus.values()[s].name());
        }
        
        return status;
    }
    
    private JComboBox loadWorkItemPriorties() {
        JComboBox priority = new JComboBox();
        for (int p = 0; p < WorkItemPriority.values().length; p++) {
            priority.addItem(WorkItemPriority.values()[p]);
        }

        return priority;
    }
    
    private JComboBox loadProjectUsers() {
        /*
         * TODO: will be loaded when constructing all existing users
         */
        JComboBox assignedUsers = new JComboBox();
        assignedUsers.addItem("mrneo");
        assignedUsers.addItem("eter");
        
        return assignedUsers;
    }
    
    private void resizeAndColorizeTable() {
        for (int c = 0; c < tableHeaders.length-1; c++) {
            if(!tableHeaders[c].equals("Subject")) { // we want to see as much as possible of subject
                witTable.getColumnModel().getColumn(c).setMinWidth(100);
                witTable.getColumnModel().getColumn(c).setMaxWidth(100);
            }
                witTable.getColumnModel().getColumn(c).setCellRenderer(tableCellRenderer);
        }
    }
    
    public static WorkItem getSelected() {
        return (WorkItem) witTable  .getModel()
                                    .getValueAt(witTable.convertRowIndexToModel(
                                                witTable.getSelectedRow()), 6);
    }
}
