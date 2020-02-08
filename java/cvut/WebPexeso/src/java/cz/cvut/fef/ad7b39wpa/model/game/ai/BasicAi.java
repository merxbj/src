/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.ai;

import cz.cvut.fef.ad7b39wpa.model.game.core.Pair;
import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.GameTurn;
import cz.cvut.fef.ad7b39wpa.model.game.core.Card;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author eTeR
 */
public class BasicAi implements ArtificialIntelligence {

    private Difficulty difficulty;
    private Game game;
    private Random rand = new Random(Calendar.getInstance().getTimeInMillis());
    private CardMemory memory;

    public BasicAi(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.memory = new CardMemory();
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void gameUpdatedByTurn(Game updatedGame, GameTurn turn) {
        if (turn.getFirst().equals(turn.getSecond())) {
            if (memory.seenCard(turn.getFirst())) {
                memory.forget(turn.getFirst()); // doesn't matter any more if card pair has been discovered already
            }
        } else {
            memory.memorize(turn.getFirst());
            memory.memorize(turn.getSecond());
        }
    }

    @Override
    public List<Card> getNextCardsToTurn() {
        double n = rand.nextDouble();
        double p = translateDifficulty();
        return getNextCardsToTurn(n, p);
    }

    private List<Card> getNextCardsToTurn(double n, double p) {
        if ((n <= p) && (memory.getKnownPairs().size() > 0)) {
            Pair<Card> cell = memory.getKnownPairs().get(rand.nextInt(memory.getKnownPairs().size()));
            return new ArrayList<Card>(cell);
        } else {
            Pair<Card> cards = new Pair<Card>(null, null);
            cards.setFirst(pickRandomCard(n, p, null));

            if ((n <= p) && memory.seenCard(cards.getFirst())) {
                Pair<Card> cell = memory.getCardCell(cards.getFirst());
                cards.setSecond(cell.getFirst());
            } else {
                cards.setSecond(pickRandomCard(n, p, cards.getFirst()));
            }

            return new ArrayList<Card>(cards);
        }
    }

    private double translateDifficulty() {
        switch (difficulty) {
            case Godlike:
                return 1.0;
            case High:
                return 0.9;
            case Medium:
                return 0.6;
            case Low:
            default:
                return 0.3;
        }
    }

    private Card pickRandomCard(double n, double p, Card exclude) {
        List<Card> notTurnedCards = game.getField().getNotTurnedCards();
        Collections.shuffle(notTurnedCards);
        if (n <= p) {
            for (Card card : notTurnedCards) {
                if (!card.hasSamePositionWith(exclude) && !memory.seenCardOnPosition(card)) {
                    return card;
                }
            }
        }

        return notTurnedCards.get(rand.nextInt(notTurnedCards.size()));
    }

    private class CardMemory implements Serializable {

        private HashMap<Integer, Pair<Card>> cells;
        private List<Pair<Card>> knownPairs;

        public CardMemory() {
            this.cells = new HashMap<Integer, Pair<Card>>();
            this.knownPairs = new LinkedList<Pair<Card>>();
        }

        public void memorize(Card card) {
            Pair<Card> cell = cells.get(card.getId());
            if (cell == null) {
                cells.put(card.getId(), new Pair<Card>(card, null));
            } else {
                if (!card.hasSamePositionWith(cell.getFirst())) {
                    cell.setSecond(card);
                    knownPairs.add(cell);
                }
            }
        }

        public void forget(Card card) {
            Pair<Card> cell = cells.remove(card.getId());
            if (cell.getSecond() != null) {
                knownPairs.remove(cell);
            }
        }

        public boolean seenCard(Card card) {
            return cells.containsKey(card.getId());
        }

        public Pair<Card> getCardCell(Card card) {
            return cells.get(card.getId());
        }

        public boolean seenCardOnPosition(Card card) {
            Pair<Card> cell = cells.get(card.getId());
            if (cell != null) {
                // ok, we might have alrady turned this card, let's check
                return (card.hasSamePositionWith(cell.getFirst())) ||
                        card.hasSamePositionWith(cell.getSecond());
            }
            return false;
        }

        public List<Pair<Card>> getKnownPairs() {
            return knownPairs;
        }

    }

}
