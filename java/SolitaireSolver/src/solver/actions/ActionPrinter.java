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
public class ActionPrinter implements ActionInterpreter {

    @Override
    public void interpret(Collection<Action> acts) {
        for (Action act : acts) {
            System.out.println(act);
        }
    }

}
