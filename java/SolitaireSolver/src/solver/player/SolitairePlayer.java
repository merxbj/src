/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.player;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import solver.actions.Action;
import solver.actions.FlipAction;
import solver.core.Card;
import solver.core.Pile;
import solver.core.Pile.Facing;
import solver.core.Table;
import solver.core.Waste;

/**
 *
 * @author jmerxbauer
 */
public class SolitairePlayer implements Player {

    private Stack<Action> actions;

    public SolitairePlayer() {
        actions = new Stack<Action>();
    }
    
    @Override
    public boolean play(Table table) {
        
        if (table.solved()) {
            return true;
        }
        
        List<Action> currentActions = generateAvailableActions(table.getWaste());
        if (currentActions.isEmpty()) {
            return false;
        }
        
        for (Action action : currentActions) {
            actions.push(action);
            action.perform(table);
            if (play(table)) {
                return true;
            } else {
                actions.pop();
            }
        }
        
        return false;
    }

    private List<Action> generateAvailableActions(Waste waste) {
        // TODO: Implement!
        List<Action> genActions = new LinkedList<Action>();
        for (Pile<Card> pile : waste.getPiles()) {
            if (pile.peek().getFacing() == Facing.Bottom) {
                genActions.add(new FlipAction(pile.getNumber()));
            } else {
                
            }
        }
        return new LinkedList<Action>();
    }

    public Stack<Action> getActions() {
        return actions;
    }
    
}
