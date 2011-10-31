/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DSA.common;

/**
 *
 * @author mrneo
 */
public class SimpleFile implements SimpleObject {
    private String name;
    private long size;

    public SimpleFile() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }
}
