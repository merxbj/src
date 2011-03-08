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
public class FoundAction implements Action {

    private int tableauNumber;

    public FoundAction(int tableauNumber) {
        this.tableauNumber = tableauNumber;
    }
    
    @Override
    public void perform(Table t) {
        t.foundTableau(tableauNumber);
    }
    
}
