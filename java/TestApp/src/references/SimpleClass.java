/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package references;

/**
 *
 * @author eTeR
 */
public class SimpleClass implements Comparable<SimpleClass> {

    public int compareTo(SimpleClass o) {
        return ((Integer) o.member).compareTo(member);
    }


    public SimpleClass(int member) {
        this.member = member;
    }

    public int member;
}
