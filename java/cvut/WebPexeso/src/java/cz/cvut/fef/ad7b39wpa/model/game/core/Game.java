/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author eTeR
 */
    public class Game extends Observable implements Serializable {
    private List<Player> players;
    private Field field;
    private int playerOnTurnIndex;

    public Game(List<Player> players, Field field) {
        this.players = players;
        this.field = field;
        this.playerOnTurnIndex = 0;
    }

    public void initialize() {
        for (Player player : players) {
            player.setGame(this);
            this.addObserver(player);
        }
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void prepareForClient() {
        if (!getGameCompleted()) {
            Player playerNextOnTurn = players.get(playerOnTurnIndex);
            GameTurn turn = playerNextOnTurn.getNextTurn();
            if (turn != null) {
                field.turn(turn.getFirst());
                field.turn(turn.getSecond());
            }
        }
    }

    public void update(Field newField) {
        setField(newField);

        List<Card> turnedCards = field.getTurnedCards();
        if (turnedCards.size() != 2) {
            throw new RuntimeException("Client side error - only two turned cards in a player turn allowed!");
        }

        Player playerOnTurn = players.get(playerOnTurnIndex);
        if (playerOnTurn != null) {
            Card oneCard = turnedCards.get(0);
            Card otherCard = turnedCards.get(1);

            setChanged();
            notifyObservers(new GameTurn(playerOnTurn, oneCard, otherCard));
            clearChanged();

            playerOnTurn.incAttempts();

            if (oneCard.equals(otherCard)) {
                oneCard.setState(Card.State.DISCOVERED);
                otherCard.setState(Card.State.DISCOVERED);
                playerOnTurn.incScore();
            } else {
                oneCard.setState(Card.State.NOT_TURNED);
                otherCard.setState(Card.State.NOT_TURNED);
                moveToNextPlayer();
            }
        }
    }

    public Player getPlayerOnTurn() {
        return players.get(playerOnTurnIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Game other = (Game) obj;
        if (this.players != other.players && (this.players == null || !this.players.equals(other.players))) {
            return false;
        }
        if (this.field != other.field && (this.field == null || !this.field.equals(other.field))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.players != null ? this.players.hashCode() : 0);
        hash = 83 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }

    private void moveToNextPlayer() {
        playerOnTurnIndex = (playerOnTurnIndex + 1) % players.size();
    }

    public boolean getGameCompleted() {
        return (field.getNotTurnedCards().isEmpty());
    }

}
