package solver.core;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Cards are immutable
 * @author jmerxbauer
 */
public class Card implements Comparable<Card> {
    
    private Suit suit;
    private Rank rank;
    private Deck deck;

    public Card(Suit suit, Rank rank, Deck deck) {
        this.suit = suit;
        this.rank = rank;
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public int compareTo(Card o) {
        if (o == null) {
            return 1;
        }
        int compare = rank.compareTo(o.rank);
        if (compare == 0) {
            return suit.compareTo(o.suit);
        }
        return compare;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Card other = (Card) obj;
        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.suit != null ? this.suit.hashCode() : 0);
        hash = 37 * hash + (this.rank != null ? this.rank.hashCode() : 0);
        return hash;
    }
    
    public enum Suit {
        Clubs, Diamonds, Hearts, Spades
    }
    
    public enum Rank {
        Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten,
        Jack, Queen, King
    }
}
