/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author eTeR
 */
public class Field implements Serializable, Comparable<Field> {

    private List<Card> cards;

    public Field(Set<Card> deck) {
        createFieldFromDeck(deck);
    }

    public Field(List<Card> cards) {
        this.cards = cards;
    }

    private void createFieldFromDeck(Set<Card> deck) {
        if (canCreateField(deck)) {
            this.cards = new ArrayList<Card>();
            this.cards.clear();
            for (Card card : deck) {
                this.cards.add(new Card(card));
                this.cards.add(new Card(card));
            }
            Collections.shuffle(this.cards);
            createFieldXYBinding(this.cards);
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
    public static Field deserialize(String serialized) {
        String[] serializedCards = serialized.split(";");
        List<Card> cards = new ArrayList<Card>(serializedCards.length);
        for (String serializedCard : serializedCards) {
            cards.add(Card.deserialize(serializedCard));
        }
        return new Field(cards);
    }

    /**
     * Let's assume that cards we have are shuffled and now are ordered
     * left to right, top to bottom on the field. Let's give this information
     * to our cards.
     */
    private void createFieldXYBinding(List<Card> cardsToBind) {
        int x = 0;
        int y = 0;
        for (Card card : cardsToBind) {
            card.setX(x++);
            card.setY(y);

            if (x == Math.round(Math.sqrt(cardsToBind.size()))) {
                x = 0;
                y++;
            }
        }
    }

    private boolean canCreateField(Set<Card> deck) {
        int cardsCount = deck.size();
        return (Math.pow(Math.round(Math.sqrt((cardsCount * 2))), 2) == (cardsCount * 2));
    }

    public Card[][] getXyfield() {
        int fieldSquareSize = getSize();
        Card[][] xyfield = new Card[fieldSquareSize][fieldSquareSize];
        for (Card card : cards) {
            xyfield[card.getY()][card.getX()] = card;
        }
        return xyfield;
    }

    public List<Card> getTurnedCards() {
        return getCardsWithState(Card.State.TURNED);
    }

    public List<Card> getNotTurnedCards() {
        return getCardsWithState(Card.State.NOT_TURNED);
    }

    public List<Card> getCardsWithState(Card.State state) {
        List<Card> notTurned = new ArrayList<Card>();
        for(Card card : cards) {
            if (card.getState() == state) {
                notTurned.add(card);
            }
        }
        return notTurned;
    }

    /**
     * Let's be naive here and compere just the serialized fields. We assume that
     * the cards retain the same order between serializations.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Field o) {
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
        final Field other = (Field) obj;
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

    public void turn(Card card) {
        Card[][] xyField = getXyfield();
        xyField[card.getY()][card.getX()].setState(Card.State.TURNED);
    }

    public int getSize() {
        return (int) Math.round(Math.sqrt(cards.size()));
    }
}
