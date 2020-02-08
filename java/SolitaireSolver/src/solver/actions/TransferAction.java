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
public class TransferAction implements Action {

    private int from;
    private int to;
    private int size;
    
    public TransferAction(int from, int to, int size) {
        this.from = from;
        this.to = to;
        this.size = size;
    }
    
    @Override
    public void perform(Table t) {
        t.transfer(from, to, size);
    }
    
}
