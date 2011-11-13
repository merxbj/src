/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mrneo
 */
public class SimpleDir implements SimpleDirInterface {
    private String name;
    private List<SimpleDirInterface> content = new ArrayList<SimpleDirInterface>();
    private boolean isSizeCalculated = false;
    private long directorySize = 0;
    private long size = 0;
    
    public SimpleDir() { }

    public String getName() {
        return name;
    }

    /*
     * Should be absolute path
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void add(SimpleDirInterface so) {
        this.content.add(so);
        this.resetCalculatedSize();
        
    }

    /*
     * Real directory size : usually something like 4096
     */
    public void setDirectorySize(long size) {
        this.directorySize = size;
    }

    public long getSize() {
        if (!isSizeCalculated) {
            for (SimpleDirInterface o : content) {
                size += o.getSize();
            }
            this.isSizeCalculated = true;
        }

        return size + directorySize;
    }
    
    public List<SimpleDirInterface> getContent() {
        return content;
    }
    
    private void resetCalculatedSize() {
        this.isSizeCalculated = false;
        this.size = 0;
    }
}
