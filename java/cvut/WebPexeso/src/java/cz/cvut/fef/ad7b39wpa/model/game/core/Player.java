/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author eTeR
 */
public class Player implements Serializable, Comparable<Player>, Observer {

    private String name;
    private int attempts;
    private int score;
    private Game game;

    public Player(String name, int attempts, int score) {
        this.name = name;
        this.attempts = attempts;
        this.score = score;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void incScore() {
        score++;
    }

    public void incAttempts() {
        attempts++;
    }

    /**
     * Regular player returns empty list. The human player will chose cards to turn
     * via the client application by himself.
     * @return
     */
    public GameTurn getNextTurn() {
        return null;
    }

    /**
     * Regular player observes the game by his eyes!
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public int compareTo(Player o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
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

}
