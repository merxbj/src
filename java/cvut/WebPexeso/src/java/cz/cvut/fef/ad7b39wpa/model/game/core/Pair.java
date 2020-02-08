/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import java.util.ArrayList;

/**
 *
 * @author eTeR
 */
public class Pair<T> extends ArrayList<T> {

    public Pair(T first, T second) {
        super(2);
        this.add(first);
        this.add(second);
    }

    public T getFirst() {
        return get(0);
    }

    public void setFirst(T first) {
        set(0, first);
    }

    public T getSecond() {
        return get(1);
    }

    public void setSecond(T second) {
        set(1, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<T> other = (Pair<T>) obj;
        if (this.getFirst() != other.getFirst() && (this.getFirst() == null || !this.getFirst().equals(other.getFirst()))) {
            return false;
        }
        if (this.getSecond() != other.getSecond() && (this.getSecond() == null || !this.getSecond().equals(other.getSecond()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.getFirst() != null ? this.getFirst().hashCode() : 0);
        hash = 53 * hash + (this.getSecond() != null ? this.getSecond().hashCode() : 0);
        return hash;
    }

}
