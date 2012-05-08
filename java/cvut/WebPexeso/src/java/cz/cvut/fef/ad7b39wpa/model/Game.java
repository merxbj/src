/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author eTeR
 */
public class Game implements Serializable, Comparable<Game> {

    private List<Card> cards;

    public Game() {
        this(new ArrayList<Card>());
    }

    public Game(List<Card> cards) {
        this.cards = cards;
    }

    public void createNewGame(Set<Card> deck) {
        if (canCreateGame(deck)) {
            cards.clear();
            for (Card card : deck) {
                cards.add(new Card(card));
                cards.add(new Card(card));
            }
            Collections.shuffle(cards);
            createFieldBinding();
        } else {
            throw new RuntimeException("You can't build a square from the cards you've got.");
        }
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (Card card : cards) {
            builder.append(card.serialize());
            builder.append(";");
        }
        return builder.toString().substring(0, builder.length() - 1); // cut off trailing ";"
    }

    /**
     * Convenience for JSP ...
     * @return
     */
    public String getSerialized() {
        return serialize();
    }

    /**
     * Factory method
     * @param serialized
     * @return
     */
    public static Game deserialize(String serialized) {
        String[] serializedCards = serialized.split(";");
        List<Card> cards = new ArrayList<Card>(serializedCards.length);
        for (String serializedCard : serializedCards) {
            cards.add(Card.deserialize(serializedCard));
        }
        return new Game(cards);
    }

    /**
     * Let's assume that cards we have have been shuffled and now are ordered
     * left to right, top to bottom on the field. Let's give this information
     * to our cards.
     */
    private void createFieldBinding() {
        int x = 0;
        int y = 0;
        for (Card card : cards) {
            card.setX(x++);
            card.setY(y);

            if (x == Math.round(Math.sqrt(cards.size()))) {
                x = 0;
                y++;
            }
        }
    }

    private boolean canCreateGame(Set<Card> deck) {
        int cardsCount = deck.size();
        return (Math.pow(Math.round(Math.sqrt((cardsCount * 2))), 2) == (cardsCount * 2));
    }

    public Card[][] getField() {
        int fieldSquareSize = (int) Math.round(Math.sqrt(cards.size()));
        Card[][] field = new Card[fieldSquareSize][fieldSquareSize];
        for (Card card : cards) {
            field[card.getY()][card.getX()] = card;
        }
        return field;
    }

    /**
     * Let's be naive here and compere just the serialized games. We assume that
     * the cards retain the same order between serializations.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Game o) {
        return this.serialize().compareTo(o.serialize());
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
        if (!this.serialize().equals(other.serialize())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.serialize().hashCode();
        return hash;
    }

}
