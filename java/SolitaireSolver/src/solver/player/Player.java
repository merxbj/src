/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.player;

import solver.core.GameProgress;
import solver.core.Table;

/**
 *
 * @author jmerxbauer
 */
public interface Player {
    public void play(Table table, GameProgress results);
}
