/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

/**
 *
 * @author jmerxbauer
 */
public class Table {

    private Deck stock;
    private Waste waste;
    private Foundation foundation;

    public Table() {
        waste = new Waste();
        foundation = new Foundation(Card.Suit.values().length);
    }
    
    /**
     * 
     * @param deck Already shuffled deck should come on top of the table.
     */
    public void setup(Deck deck, int pileCount) {
        stock = deck;
        waste.setup(stock, pileCount);
    }
    
    public void dealStock() {
        waste.deal(stock);
    }
    
}