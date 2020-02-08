package cz.merxbj.unip.gui.tt;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeTableModel implements TreeModel {

    protected Object root;
    protected EventListenerList listenerList = new EventListenerList();

    public AbstractTreeTableModel(Object root) {
        this.root = root;
    }

    public void setRoot(Object root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }

    public abstract int getColumnCount();

    public abstract String getColumnName(int column);

    public abstract Object getValueAt(Object node, int column);
    
    /**
     * Returns column width set by its implementation
     * 0 = do not handle column width
     * @return int
     */
    public abstract int getColumnWidth(int column);

    public Class getColumnClass(int column) {
        return Object.class;
    }

    public boolean isCellEditable(Object node, int column) {
        return getColumnClass(column) == AbstractTreeTableModel.class;
    }

    public void setValueAt(Object aValue, Object node, int column) {
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
}