/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.LinkedList;
import java.util.List;

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
            for (int j = i + 1; j >= 0; j--) {
                Pile.Facing facing = Pile.Facing.Bottom;
                if (j == 0) {
                    facing = Pile.Facing.Top;
                }
            }
        }
    }
    
    public void deal(Deck stock) {
        
    }

}
