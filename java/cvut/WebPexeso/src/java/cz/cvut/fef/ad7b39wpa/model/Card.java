/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author eTeR
 */
public class Card implements Serializable, Comparable<Card> {

    private int x;
    private int y;
    private int id;
    private State state;

    public Card(int id) {
        this(-1, -1, id, State.NOT_TURNED);
    }

    public Card(int x, int y, int id, State state) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.state = state;
    }

    public Card(final Card other) {
        this.x = other.x;
        this.y = other.y;
        this.id = other.id;
        this.state = other.state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String serialize() {
        return String.format("%d,%d,%d,%d", x, y, id, state.getStateId());
    }

    @Override
    public int compareTo(Card o) {
        return ((Integer)this.id).compareTo(o.id);
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.id;
        return hash;
    }

    /**
     * Factory method. This is a little bit too much :-D
     * @param serialized
     * @return
     */
    public static Card deserialize(String serialized) {
        String[] attributes = serialized.split(",");
        return new Card(Integer.parseInt(attributes[0]),
                        Integer.parseInt(attributes[1]),
                        Integer.parseInt(attributes[2]),
                        State.fromStateId(Integer.parseInt(attributes[3])));
    }

    @Override
    public String toString() {
        return "Card{" + "x=" + x + "y=" + y + "id=" + id + "state=" + state + '}';
    }

    public enum State {
        NOT_TURNED(0), TURNED(1), DISCOVERED(2), DISCOVER_COMMITED(3);

        private int stateId;
        private static final Map<Integer, State> idToState;

        static {
            idToState = new HashMap<Integer, State>();
            for (int i = 0; i < State.values().length; i++) {
                idToState.put(i, State.values()[i]);
            }
        }

        private State(int stateId) {
            this.stateId = stateId;
        }

        public int getStateId() {
            return stateId;
        }

        public static State fromStateId(int stateId) {
            return idToState.get(stateId);
        }

    }

}
