package cz.merxbj.unip.gui.tt;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;


public class TableModel extends AbstractTableModel implements TreeExpansionListener, TreeModelListener {

    private JTree tree;
    private AbstractTreeTableModel treeTableModel;

    public TableModel(AbstractTreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

        this.registerListeners();
    }

    private void registerListeners() {
        tree.addTreeExpansionListener(this);
        treeTableModel.addTreeModelListener(this);
    }

    @Override
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    @Override
    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    @Override
    public int getRowCount() {
        return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }
    
    public void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        fireTableDataChanged();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        fireTableDataChanged();
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        delayedFireTableDataChanged();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        delayedFireTableDataChanged();
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        delayedFireTableDataChanged();
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        delayedFireTableDataChanged();
    }
}
