/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.Tree;

import DA.common.FileSystemViewExt;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author mrneo
 */
public class TreeCellRenderer extends DefaultTreeCellRenderer {

    private FileSystemViewExt fsve = new FileSystemViewExt();
    private Map<String, Icon> iconCache = new HashMap<String, Icon>();

    @Override
    public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        TreeNodeExt treeNode = (TreeNodeExt) value;
        File file = treeNode.getFile();
        String fileName = treeNode.getName();

        JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, fileName, sel, expanded, leaf, row, hasFocus);
        if (file != null) {
            result.setIcon(getFileIcon(file, fileName));
        }
        return result;
    }

    private Icon getFileIcon(File file, String fileName) {
        Icon icon = this.iconCache.get(fileName);
        if (icon == null) {
            icon = fsve.getSystemIcon(file);
            this.iconCache.put(fileName, icon);
        }
        return icon;
    }
}