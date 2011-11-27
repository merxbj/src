/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.Tree;

import DA.common.FileSystemViewExt;
import DA.TreeMap.SimpleFile;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author mrneo
 */
public class RootsTree extends JTree {

    public static final String LOADING_TEXT = "Loading ...";
    //DefaultMutableTreeNode rootsTree = new DefaultMutableTreeNode("ROOTS");
    TreeNodeExt root;

    public RootsTree() {
        super();
        this.init();
    }

    private void init() {
        setEditable(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setShowsRootHandles(true);
        setRootVisible(false);

        this.addRootElements();
        this.setModel(new DefaultTreeModel(root));

        this.setCellRenderer(new TreeCellRenderer());
    }

    private void addRootElements() {
        FileSystemViewExt fsve = new FileSystemViewExt();
        File[] rootsList = fsve.getRoots();
        root = new TreeNodeExt(rootsList);
    }

    public SimpleFile getSelected() {
        try {
            TreeNodeExt node = (TreeNodeExt) this.getSelectionPath().getLastPathComponent();
            SimpleFile selectedFile = new SimpleFile(node.getFile());
            return selectedFile;
        } catch (Exception e) {
            return null;
        }
    }
}
