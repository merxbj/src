/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jmerxbauer
 */
public class Deck {
    private static final int DECK_MIN_SIZE = 32;
    
    private List<Card> cards;

    public Deck() {
        this(DECK_MIN_SIZE);
    }

    public Deck(int size) {
        this.cards = new ArrayList<Card>(size);
    }

    public void push(Card c) {
        cards.add(c);
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public Card pop() {
        return cards.remove(cards.size() - 1);
    }
    
    public Card pull(Card c) {
        int index = cards.indexOf(c);
        if (index > -1) {
            return cards.remove(index);
        }
        return null;
    }
}
