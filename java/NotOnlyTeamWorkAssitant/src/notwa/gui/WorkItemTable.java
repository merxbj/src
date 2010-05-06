/*
 * WorkItemTable
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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import notwa.common.EventHandler;

import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;

public class WorkItemTable extends JComponent implements ListSelectionListener {

    private static final String[] tableHeaders = {"Product", "WIT ID", "Subject", "Priority", "Assigned", "Status", "WI"};
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private JTable witTable;
    private TblModel witTableModel;
    private TableRowSorter<TblModel> sorter;
    private EventHandler<GuiEvent> guiHandler;

    public WorkItemTable(WorkItemCollection wic) {
        init(wic);
    }

    private void init(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());

        witTableModel = new TblModel(wic, tableHeaders);
        witTable = new JTable();
        witTable.setModel(witTableModel);
        witTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<TblModel>(witTableModel);
        witTable.setRowSorter(sorter);

        witTable.getSelectionModel().addListSelectionListener(this);

        this.resizeAndColorizeTable();

        /*
         * Hide last column containing whole WorkItem for WorkItemDetail
         */
        witTable.getColumnModel().removeColumn(witTable.getColumnModel().getColumn(6));

        JScrollPane jsp = new JScrollPane(witTable);

        this.add(jsp, BorderLayout.CENTER);
    }

    public void onFireSelectedRowChanged(EventHandler<GuiEvent> handler) {
        this.guiHandler = handler;
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
    
    public WorkItem getSelected() {
        return (WorkItem) this.witTable.getModel()
                                       .getValueAt(witTable.convertRowIndexToModel(
                                                    witTable.getSelectedRow()), 6);
    }
    
    public TableRowSorter<TblModel> getSorter() {
        return this.sorter;
    }
    
    public void refresh() {
        refreshTable();
        refreshDetail();
    }

    public void refreshTable() {
        witTableModel.fireTableDataChanged();
    }

    public void refreshDetail() {
        
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        GuiEventParams gep = new GuiEventParams(GuiEventParams.SELECTED_ROW_CHANGED, this.getSelected());
        guiHandler.handleEvent(new GuiEvent(gep));
    }
}
