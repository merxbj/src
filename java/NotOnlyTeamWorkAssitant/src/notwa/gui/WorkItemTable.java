package notwa.gui;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;

public class WorkItemTable extends TabContent {
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
                WorkItemDetail.getInstance().updateDisplayedData();
                WorkItemNoteHistoryTable.getInstance().updateDisplayedData();
            }
        });
        
        this.resizeAndColorizeTable();

        /*
         * Hide last column containing whole WorkItem for WorkItemDetail
         */
        witTable.getColumnModel().removeColumn(witTable.getColumnModel().getColumn(6));
        
        JScrollPane jsp = new JScrollPane(witTable);
        
        this.add(jsp, BorderLayout.CENTER);
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
