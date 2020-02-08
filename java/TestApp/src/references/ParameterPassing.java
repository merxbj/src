/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package references;

/**
 *
 * @author eTeR
 */

import java.util.*;

public class ParameterPassing {
    
    public static void main(String[] args) {

        ArrayList<SimpleClass> sc = new ArrayList<SimpleClass>();

        sc.add(new SimpleClass(7));
        sc.add(new SimpleClass(2));
        sc.add(new SimpleClass(5));
        sc.add(new SimpleClass(10));

        Collections.sort(sc, new SimpleClassComp());

        for (SimpleClass sc1 : sc) {
            System.out.println(String.format("%d", sc1.member));
        }

        String[] str = new String[10];
    }
}
