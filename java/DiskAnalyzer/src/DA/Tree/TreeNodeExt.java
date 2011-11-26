/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.Tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.tree.TreeNode;

/**
 *
 * @author mrneo
 */
public class TreeNodeExt implements TreeNode {

    private File file;
    private List<File> children;
    private TreeNodeExt parent;
    private boolean isRoot;

    public TreeNodeExt(File file, boolean isRoot, TreeNodeExt parent) {
        this.file = file;
        this.isRoot = isRoot;
        this.parent = parent;
        this.children = getFileChilds(file.listFiles());
    }

    public TreeNodeExt(File[] children) {
        this.file = null;
        this.parent = null;
        this.children = getFileChilds(children);
    }

    private List<File> getFileChilds(File[] fileContent) {
        if (fileContent != null) {
            if (fileContent.length > 0) {
                Arrays.sort(fileContent);
                List<File> newFileContent = new ArrayList<File>();
                /* This should do something like OS sort (folders first and files after */
                AddFiles(fileContent,newFileContent, true);  /* Append to newFileContent only folders */
                AddFiles(fileContent,newFileContent, false); /* Append to newFileContent only files */
                
                return newFileContent;
            }
        }
        return null;
    }
    
    private void AddFiles(File[] from, List<File> to, boolean foldersOnly) {
        for (int i = 0; i < from.length; i++) {
            if (from[i].isDirectory() && foldersOnly) {
                to.add(from[i]);
            }
            else if (from[i].isFile() && !foldersOnly) {
                to.add(from[i]);
            }
        }
    }

    public Enumeration<?> children() {
        final int elementCount = this.children.size();
        return new Enumeration<File>() {

            int count = 0;

            public boolean hasMoreElements() {
                return this.count < elementCount;
            }

            public File nextElement() {
                if (this.count < elementCount) {
                    return children.get(this.count++);
                }
                throw new NoSuchElementException("Oups! We have a probem!");
            }
        };
    }

    public boolean getAllowsChildren() {
        return file.isDirectory();
    }

    public TreeNodeExt getChildAt(int childIndex) {
        return new TreeNodeExt(children.get(childIndex), parent == null, this);
    }

    public int getChildCount() {
        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getIndex(TreeNode node) {
        TreeNodeExt treeNode = (TreeNodeExt) node;
        for (int i = 0; i < children.size(); i++) {
            if (treeNode.getFile().equals(children.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public TreeNodeExt getParent() {
        return this.parent;
    }

    public boolean isLeaf() {
        return (this.getChildCount() == 0);
    }

    public String getName() {
        if (file != null) {
            if (isRoot()) {
                return file.getAbsolutePath();

            } else {
                return file.getName();
            }
        }
        return "";
    }

    public File getFile() {
        return this.file;
    }

    public boolean isRoot() {
        return isRoot;
    }
}
