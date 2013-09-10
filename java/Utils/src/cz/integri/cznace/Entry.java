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
    private String code;
    private String name;

    public Entry(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
