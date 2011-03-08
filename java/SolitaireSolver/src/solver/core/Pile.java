/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author jmerxbauer
 */
public class Pile<E extends Card> implements Comparable<Pile<E>>, Iterable<E> {

    private Stack<FacedCard> pile;
    private int number;

    public Pile() {
        this(-1);
    }

    public Pile(int number) {
        this.pile = new Stack<FacedCard>();
        this.number = number;
    }

    @Override
    public int compareTo(Pile<E> o) {
        return ((Integer) number).compareTo(o.number);
    }
    
    public void stack(E card) {
        stack(card, Facing.Top);
    }
    
    public void stack(E card, Facing facing) {
        pile.push(new FacedCard(card, facing));
    }
    
    public FacedCard pop() {
        return pile.pop();
    }
    
    public int size() {
        return pile.size();
    }

    public int getNumber() {
        return number;
    }
    
    public FacedCard peek() {
        return pile.peek();
    }
    
    public Stack<FacedCard> getPile() {
        return pile;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            Iterator<FacedCard> innerIterator = pile.iterator();
            
            @Override
            public boolean hasNext() {
                return innerIterator.hasNext();
            }

            @Override
            public E next() {
                FacedCard next = innerIterator.next();
                return next != null ? next.getCard() : null;
            }

            @Override
            public void remove() {
                innerIterator.remove();
            }
        };
    }
    
    public class FacedCard {
        private E card;
        private Facing facing;

        public FacedCard(E card, Facing facing) {
            this.card = card;
            this.facing = facing;
        }

        public E getCard() {
            return card;
        }

        public Facing getFacing() {
            return facing;
        }
        
    }
    
    public enum Facing {
        Top, Bottom
    }

}
