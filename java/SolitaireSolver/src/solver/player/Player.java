/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.player;

import solver.core.GameResults;
import solver.core.Table;

/**
 *
 * @author jmerxbauer
 */
public interface Player {
    public GameResults play(Table table);
}
