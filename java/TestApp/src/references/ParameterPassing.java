/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package references;

/**
 *
 * @author eTeR
 */
public class ParameterPassing {
    public static void main(String[] args) {

        SimpleClass sc = new SimpleClass(1);

        System.out.println(String.format("%d", sc.member));
        modify(sc);
        System.out.println(String.format("%d", sc.member));

    }

    private static void modify(SimpleClass o) {
        o = new SimpleClass(2);
    }
}
