/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author eTeR
 */
public class CardDeckFactory {
    public static Set<Card> createBasicCardDeck() {
        Set<Card> deck = new HashSet<Card>(32);
        for (int id = 0; id < 32; id++) {
            deck.add(new Card(id));
        }
        return deck;
    }
}
