/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.actions;

import java.util.Collection;

/**
 *
 * @author jmerxbauer
 */
public interface ActionInterpreter {
    public void interpret(Collection<Action> acts);
}
