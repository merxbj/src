/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.actions;

import solver.core.Table;
import solver.core.Waste;

/**
 *
 * @author jmerxbauer
 */
public class FlipAction implements Action {

    private int tableauNumber;

    public FlipAction(int tableauNumber) {
        this.tableauNumber = tableauNumber;
    }

    @Override
    public void perform(Table t) {
        t.flipWasteCardOnTableau(tableauNumber);
    }
    
}
