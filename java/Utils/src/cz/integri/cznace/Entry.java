/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.integri.cznace;

/**
 *
 * @author mexbik
 */
public class Entry {
    private long code;
    private String name;

    public Entry(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
