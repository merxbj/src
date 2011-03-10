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

        return completeTableau.isTransferable();
    }

    public int getAvailablePiles() {
        return availablePiles;
    }

}
