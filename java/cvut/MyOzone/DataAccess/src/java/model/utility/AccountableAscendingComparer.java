/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.utility;

import java.util.Comparator;
import model.Accountable;

/**
 *
 * @author eTeR
 */
public class AccountableAscendingComparer implements Comparator<Accountable> {

    @Override
    public int compare(Accountable o1, Accountable o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

}
