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

public class SimpleClassComp implements Comparator<SimpleClass> {

    public int compare(SimpleClass o1, SimpleClass o2) {
        return o1.compareTo(o2);
    }

}
