/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

/**
 *
 * @author jmerxbauer
 */
public class FrenchPrimaryDeckFactory implements DeckFactory {

    private static final int FRENCH_PRIMARY_DECK_SIZE = 52;
    
    @Override
    public Deck createDeck() {
        Deck deck = new Deck(FRENCH_PRIMARY_DECK_SIZE);
        
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                Card card = new Card(suit, rank, deck);
                deck.push(card);
            }
        }

        return deck;
    }

}
