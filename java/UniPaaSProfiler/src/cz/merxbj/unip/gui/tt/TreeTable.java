package cz.merxbj.unip.gui.tt;

import cz.merxbj.unip.core.LogTreeTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableColumn;

public class TreeTable extends JTable {

    private TreeTableCellRenderer tree;
    private AbstractTreeTableModel treeTableModel;
    private TableModel tableModel;

    public TreeTable() {
        this(new LogTreeTableModel("Empty JTreeTable"));
    }

    public TreeTable(AbstractTreeTableModel treeTableModel) {
        super();
        this.treeTableModel = treeTableModel;
        this.initComponents();
    }

    private void initComponents() {
        tree = new TreeTableCellRenderer(treeTableModel, this);

        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper(tree);
        tree.setSelectionModel(selectionWrapper);
        this.setSelectionModel(selectionWrapper.getListSelectionModel());

        tableModel = new TableModel(treeTableModel, tree);
        super.setModel(tableModel);
        this.setDefaultRenderer(AbstractTreeTableModel.class, tree);
        this.setDefaultEditor(AbstractTreeTableModel.class, new TreeTableCellEditor(tree, this));
        this.setShowGrid(false);
        this.setComponentPopupMenu(new TableContextMenu());
        this.setupColumns();
        this.setDragEnabled(false);

    }

    private void setupColumns() {
        for (int i = 0; i < treeTableModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            int columnWidth = treeTableModel.getColumnWidth(i);
            if (columnWidth != 0) {
                this.setColumnSize(column, columnWidth);
                if (!column.equals("")) {
                    column.setResizable(false);
                }
            }
        }
    }

    private void setColumnSize(TableColumn column, int size) {
        column.setWidth(size);
        column.setPreferredWidth(size);
        column.setMaxWidth(size);
        column.setMinWidth(size);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
        }

        //Set same look and feel for both componenets
        LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
    }

    @Override
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == AbstractTreeTableModel.class) ? -1 : editingRow;
    }

    @Override
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    public JTree getTree() {
        return tree;
    }

    public AbstractTreeTableModel getTreeTableModel() {
        return treeTableModel;
    }

    class TableContextMenu extends JPopupMenu implements ActionListener {

        JMenuItem menuExpandItem;
        JMenuItem menuCollapseItem;

        public TableContextMenu() {
            this.initComponents();
        }

        private void initComponents() {
            menuExpandItem = new JMenuItem("Expand all");
            menuExpandItem.addActionListener(this);
            menuCollapseItem = new JMenuItem("Collapse all");
            menuCollapseItem.addActionListener(this);

            this.add(menuExpandItem);
            this.add(menuCollapseItem);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == menuExpandItem) {
                for (int i = 0; i < tree.getRowCount(); i++) {
                    tree.expandRow(i);
                }
            } else if (ae.getSource() == menuCollapseItem) {
                for (int i = 0; i < tree.getRowCount(); i++) {
                    tree.collapseRow(i);
                }
            }
        }
    }
}