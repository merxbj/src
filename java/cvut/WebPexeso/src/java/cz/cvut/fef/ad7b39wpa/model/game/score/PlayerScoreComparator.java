/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.score;

import cz.cvut.fef.ad7b39wpa.model.game.core.Player;
import java.util.Comparator;

/**
 *
 * @author eTeR
 */
public class PlayerScoreComparator implements Comparator<Player> {

    @Override
    public int compare(Player o1, Player o2) {
        return ((Integer) o1.getScore()).compareTo(o2.getScore());
    }

}
