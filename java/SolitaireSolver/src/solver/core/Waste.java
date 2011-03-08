/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.LinkedList;
import java.util.List;
import solver.core.Pile.FacedCard;

/**
 *
 * @author jmerxbauer
 */
public class Waste {

    private List<Pile<Card>> piles;
    
    public Waste() {
        piles = new LinkedList<Pile<Card>>();
    }

    /**
     * 
     * @param deck Initial (full) deck of cards.
     */
    public void setup(Deck deck, int pileCount) {
        
        // setup each pile of cards with increasing card count
        for (int i = 0; i < pileCount; i++) {
            Pile<Card> pile = new Pile<Card>(i);
            for (int j = i + 1; j >= 0; j--) {
                Pile.Facing facing = Pile.Facing.Bottom;
                if (j == 0) {
                    facing = Pile.Facing.Top;
                }
                pile.stack(deck.pop(), facing);
            }
            piles.add(pile);
        }
    }
    
    public void deal(Deck stock) {
        if (!readyToDealStock()) {
            throw new RuntimeException("Attempted to deal a stock when it is not possible!");
        }
        
        for (Pile<Card> pile : piles) {
            Card card = stock.pop();
            if (card == null) {
                break; // we have dealt everything from the stock
            }
            pile.stack(card, Pile.Facing.Top);
        }
    }
    
    public List<Pile<Card>> getPiles() {
        return piles;
    }

    private boolean readyToDealStock() {
        int emptyPiles = 0;
        int totalCardsLeft = 0;

        for (Pile<Card> pile : piles) {
            totalCardsLeft += pile.size();
            if (pile.size() == 0) {
                emptyPiles++;
            }
        }
        
        return ((emptyPiles != 0) || (emptyPiles > totalCardsLeft));
    }
    
    public int size() {
        int size = 0;
        for (Pile<Card> pile : piles) {
            size += pile.size();
        }
        return size;
    }

    void flipCardOnTableau(int tableauNumber) {
        Pile<Card> pileToFlipCardOn = piles.get(tableauNumber);
        FacedCard faced = pileToFlipCardOn.pop();
        if (faced.getFacing().equals(Pile.Facing.Top)) {
            pileToFlipCardOn.stack(faced.getCard(), faced.getFacing());
            throw new RuntimeException("Attempted to flip already top-facing card!");
        }
        pileToFlipCardOn.stack(faced.getCard(), Pile.Facing.Top);
    }

}
