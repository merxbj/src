/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import java.io.Serializable;

/**
 *
 * @author eTeR
 */
public class GameTurn extends Pair<Card> implements Serializable {

    private Player player;

    public GameTurn(Player player, Card first, Card second) {
        super(first, second);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameTurn other = (GameTurn) obj;
        if (this.player != other.player && (this.player == null || !this.player.equals(other.player))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5 + super.hashCode();
        hash = 29 * hash + (this.player != null ? this.player.hashCode() : 0);
        return hash;
    }
    
}
