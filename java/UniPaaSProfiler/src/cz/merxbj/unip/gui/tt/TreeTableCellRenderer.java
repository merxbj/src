package cz.merxbj.unip.gui.tt;

import cz.merxbj.unip.common.CommonStatics;
import cz.merxbj.unip.core.LogEvent;
import cz.merxbj.unip.core.LogEventType;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

    private final TreeTable treeTable;
    private int visibleRow;
    private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    private TreeCellRenderer treeCellRenderer = getCellRenderer();

    public TreeTableCellRenderer(TreeModel model, TreeTable treeTable) {
        super(model);
        this.treeTable = treeTable;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        // use same setting for both components
        if (treeCellRenderer instanceof DefaultTreeCellRenderer) {
               DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) treeCellRenderer);

            dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
            dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
            dtcr.setBackground(this.getBackground());
        }
    }

    @Override
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight());
        super.paint(g);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color rowColor = (isSelected) ? table.getSelectionBackground() : this.getRowColor(row);
        visibleRow = row;

        this.setBackground(rowColor);
        renderer.setBackground(rowColor);
        return (column == 0) ? this : renderer; // first column is column with JTree so we must send apropriate renderer
    }

    private Color getRowColor(int row) {
        TreePath tree = treeTable.getTree().getPathForRow(row);
        LogEvent event = (LogEvent) tree.getLastPathComponent();
        if (event != null) {
            return LogEventType.getColor(event);
        }
        return CommonStatics.DEFAULT_BACKGROUND;
    }
}
