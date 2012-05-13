/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.score;

import java.io.Serializable;

/**
 *
 * @author eTeR
 */
public class PlayerScore implements Serializable, Comparable<PlayerScore> {
    private String name;
    private int games;
    private int wins;

    public PlayerScore(String name, int games, int wins) {
        this.name = name;
        this.games = games;
        this.wins = wins;
    }

    public String getName() {
        return name;
    }

    public int getGames() {
        return games;
    }

    public int getWins() {
        return wins;
    }

    public void incWins() {
        wins++;
    }

    public void incGames() {
        games++;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerScore other = (PlayerScore) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(PlayerScore o) {
        return ((Integer) wins).compareTo(o.wins);
    }

}
