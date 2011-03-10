/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

/**
 *
 * @author jmerxbauer
 */
public class SpideretteSolitaire {

    private static final int SPIDERETTE_SOLITAIRE_WASTE_PILE_COUNT = 7;
    
    private Table table;

    public SpideretteSolitaire() {
        this.table = new Table();
    }
    
    public Table initNewGame(DeckFactory factory) {
        Deck deck = factory.createDeck();
        deck.shuffle();
        
        table.setup(deck, SPIDERETTE_SOLITAIRE_WASTE_PILE_COUNT);
        return table;
    }
    
    
}
