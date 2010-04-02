package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import notwa.wom.NoteCollection;

public class WorkItemNoteHistoryTable extends WorkItemDetailLayout{
    private static WorkItemNoteHistoryTable instance;
    private TblModel nhTableModel;
    private JTable nhTable;
    private String[] tableHeaders = {"Note author", "Text"};
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    
    private NoteCollection noteCollection;
    
    public static WorkItemNoteHistoryTable getInstance() {
        if (instance == null) {
            instance = new WorkItemNoteHistoryTable();
        }
        return instance;
    }

    public JComponent initNoteHistoryTable() {
        this.setLayout(new GridLayout(1,0));

        nhTableModel = new TblModel(noteCollection, tableHeaders);
        nhTable = new JTable(nhTableModel);
        nhTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.resizeAndColorizeTable();
        
        JScrollPane jsp = new JScrollPane(nhTable);

        this.add(jsp);
        
        return this;
    }
    
    private void resizeAndColorizeTable() {
                nhTable.getColumnModel().getColumn(0).setMaxWidth(100);
                nhTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
                nhTable.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
    }
    
    public void setNoteCollection(NoteCollection nc) {
        try {
            nhTable.setModel(new TblModel(nc, tableHeaders));
            this.resizeAndColorizeTable();
        } catch (Exception e) {};        
    }

    public void setAllToNull() {
        this.setNoteCollection(null);
    }
}