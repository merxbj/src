/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author jmerxbauer
 */
public class Pile<E extends Card> implements Iterable<E>, Comparable<Pile<E>> {

    private Map<E, Facing> pile;
    private int number;

    public Pile() {
        this(-1);
    }
    
    public Pile(int number) {
        this.pile = new HashMap<E, Facing>();
        this.number = number;
    }
    
    @Override
    public Iterator<E> iterator() {
        return pile.keySet().iterator();
    }

    @Override
    public int compareTo(Pile<E> o) {
        return ((Integer) number).compareTo(o.number);
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

    public int getNumber() {
        return number;
    }
    
    public Map<E, Facing> getPile() {
        return pile;
    }
    
    public Pile<E> moveableSublist() {
        // TODO: Implement
        throw new NotImplementedException();
    }
    
    public enum Facing {
        Top, Bottom
    }

}
