/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.player;

import java.util.List;
import java.util.Stack;
import solver.actions.Action;
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Stack<Action> getActions() {
        return actions;
    }
    
}
