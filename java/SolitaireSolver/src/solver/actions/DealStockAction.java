/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.actions;

import solver.core.Table;

/**
 *
 * @author jmerxbauer
 */
public class DealStockAction implements Action {

    @Override
    public void perform(Table t) {
        t.dealStock();
    }

}
