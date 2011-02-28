/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.player;

import java.util.Collections;
import java.util.List;
import solver.core.Card;
import solver.core.GameProgress;
import solver.core.Pile;
import solver.core.PileUnflippedCountComparator;
import solver.core.Table;
import solver.core.Waste;

/**
 *
 * @author jmerxbauer
 */
public class SolitairePlayer implements Player {

    @Override
    public void play(Table table, GameProgress results) {
        
        Waste waste = table.getWaste();
        List<Pile<Card>> piles = waste.getPiles();
        Collections.sort(piles, new PileUnflippedCountComparator());
        
        while (true) {
            
            
            
        }
        
    }
    
}
