/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jmerxbauer
 */
public class Foundation {
    private int availablePiles;
    private List<Pile<Card>> usedUpPiles;

    public Foundation(int piles) {
        this.availablePiles = piles;
        this.usedUpPiles = new LinkedList<Pile<Card>>();
    }
    
    public int found(Pile<Card> completeTableau) {
        if (!validateTableau(completeTableau)) {
            throw new RuntimeException("This is not a complete tableau!");
        }

        if (availablePiles-- > 0) {
            usedUpPiles.add(completeTableau);
        }
        
        return usedUpPiles.size();
    }

    private boolean validateTableau(Pile<Card> completeTableau) {
        if (completeTableau.size() != Card.Rank.values().length) {
            return false;
        }
        
        Map<Card.Suit, Integer> suitsInTableau = new EnumMap<Card.Suit, Integer>(Card.Suit.class);
        Card.Rank expectedRank = Card.Rank.Ace;
        for (Card card : completeTableau) {
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

    public int getAvailablePiles() {
        return availablePiles;
    }

}
