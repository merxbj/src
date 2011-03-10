/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author jmerxbauer
 */
public class Pile<E extends Card> implements Comparable<Pile<E>>, Iterable<E> {

    private Deque<FacedCard> pile;
    private int number;

    public Pile() {
        this(-1);
    }

    public Pile(int number) {
        this.pile = new LinkedList<FacedCard>();
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
        pile.addFirst(new FacedCard(card, facing));
    }
    
    public void stack(Pile<E> cards) {
        FacedCard element = cards.getPile().removeLast();
        while (element != null) {
            if (element.getFacing() != Facing.Top) {
                throw new RuntimeException("Attempted to stack bottom-facing card!");
            }
            pile.addFirst(element);
            element = cards.getPile().removeLast();
        }
    }
    
    public void remove(Pile<E> cards) {
        int toRemove = cards.size();
        while (toRemove-- > 0) {
            pile.removeFirst();
        }
    }
    
    public FacedCard pop() {
        return pile.removeFirst();
    }
    
    public int size() {
        return pile.size();
    }

    public int getNumber() {
        return number;
    }
    
    public FacedCard peek() {
        return pile.peekFirst();
    }
    
    public Deque<FacedCard> getPile() {
        return pile;
    }
    
    public Pile<E> subPile(int size) {
        Pile<E> subPile = new Pile<E>();
        for (FacedCard element : pile) {
            subPile.getPile().addLast(element);
            if (subPile.size() == size) {
                break;
            }
        }
        return subPile;
    }
    
    public boolean isTransferable() {
        Map<Card.Suit, Integer> suitsInTableau = new EnumMap<Card.Suit, Integer>(Card.Suit.class);
        Card.Rank expectedRank = Card.Rank.Ace;
        for (Card card : this) {
            if (card.getRank() != expectedRank) {
                return false;
            }
            
            int suitCount = 0;
            if (suitsInTableau.containsKey(card.getSuit())) {
                suitCount = suitsInTableau.get(card.getSuit());
            }
            suitsInTableau.put(card.getSuit(), ++suitCount);
            
            expectedRank = Card.Rank.values()[expectedRank.ordinal() + 1];
        }
        
        if (suitsInTableau.size() > 1) {
            return false;
        }

        return true;
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
