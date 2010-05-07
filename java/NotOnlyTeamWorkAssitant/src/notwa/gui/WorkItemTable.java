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
import notwa.gui.tablemodels.ColumnSettings;
import notwa.gui.tablemodels.WorkItemlModel;

import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;

public class WorkItemTable extends JComponent implements ListSelectionListener {

    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private JTable witTable;
    private WorkItemlModel witTableModel;
    private TableRowSorter<WorkItemlModel> sorter;
    private EventHandler<GuiEvent> guiHandler;
    private WorkItemCollection wic;

    public WorkItemTable(WorkItemCollection wic) {
        init(wic);
    }

    private void init(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        this.wic = wic;

        witTableModel = new WorkItemlModel(wic);
        witTable = new JTable();
        witTable.setModel(witTableModel);
        witTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<WorkItemlModel>(witTableModel);
        witTable.setRowSorter(sorter);

        witTable.getSelectionModel().addListSelectionListener(this);

        this.resizeAndColorizeTable();

        JScrollPane jsp = new JScrollPane(witTable);

        this.add(jsp, BorderLayout.CENTER);
    }

    public void onFireSelectedRowChanged(EventHandler<GuiEvent> handler) {
        this.guiHandler = handler;
    }
    
    private void resizeAndColorizeTable() {
        
        for (int c = 0; c < witTableModel.getColumnCount() ; c++) {

            ColumnSettings<?> cs = witTableModel.getColumnSettings(c);
            if (cs.getColumnAlias() != WorkItemlModel.WorkItemTableColumn.COLUMN_WORK_ITEM_SUBJECT_ALIAS) {
                /**
                 * We want to see as much as possible of subject so shorten all else
                 */
                witTable.getColumnModel().getColumn(c).setMinWidth(100);
                witTable.getColumnModel().getColumn(c).setMaxWidth(100);
            }

            witTable.getColumnModel().getColumn(c).setCellRenderer(tableCellRenderer);
        }
    }
    
    public WorkItem getSelected() {
        int selectedIndex = witTable.convertRowIndexToModel(witTable.getSelectedRow());
        return wic.get(selectedIndex);
    }
    
    public TableRowSorter<WorkItemlModel> getSorter() {
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
