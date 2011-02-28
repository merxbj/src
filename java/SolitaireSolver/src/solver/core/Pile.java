/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author jmerxbauer
 */
public class Pile<E extends Card> implements Iterable<E> {

    private HashMap<E, Facing> pile;

    public Pile() {
        this.pile = new HashMap<E, Facing>();
    }
    
    @Override
    public Iterator<E> iterator() {
        return pile.keySet().iterator();
    }
    
    public void stack(E card) {
        stack(card, Facing.Top);
    }
    
    public void stack (E card, Facing facing) {
        pile.put(card, facing);
    }
    
    public int size() {
        return pile.size();
    }
    
    public enum Facing {
        Top, Bottom
    }

}
