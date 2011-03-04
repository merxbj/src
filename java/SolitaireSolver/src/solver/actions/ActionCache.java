/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.actions;

/**
 *
 * @author jmerxbauer
 */
public class ActionCache {
    private static final TransferAction transferAction;
    private static final FlipAction flipAction;
    private static final DealDeckAction dealDeckAction;
    private static final FoundAction foundAction;
    
    static {
        transferAction = new TransferAction();
        flipAction = new FlipAction();
        dealDeckAction = new DealDeckAction();
        foundAction = new FoundAction();
    }

    public static DealDeckAction getDealDeckAction() {
        return dealDeckAction;
    }

    public static FlipAction getFlipAction() {
        return flipAction;
    }

    public static FoundAction getFoundAction() {
        return foundAction;
    }

    public static TransferAction getTransferAction() {
        return transferAction;
    }
    
}
