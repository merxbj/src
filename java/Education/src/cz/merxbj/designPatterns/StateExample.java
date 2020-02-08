/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.designPatterns;

/**
 *
 * @author jmerxbauer
 */
public class StateExample {
    
    interface State {
        void performAction(Context context);
    }
    
    class UnstableState implements State {
        public void performAction(Context context) {
            context.curr = new StableState();
        }
    }

    class StableState implements State {
        public void performAction(Context context) {
            System.out.println("Stable, finally!");
        }
    }

    class Context {
        public State curr = new UnstableState();

        public void poke() {
            curr.performAction(this);
        }
    }

    public void use() {
        Context c = new Context();
        c.poke();
        c.poke();
    }
}
