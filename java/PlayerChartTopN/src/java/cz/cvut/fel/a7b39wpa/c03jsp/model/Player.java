package cz.cvut.fel.a7b39wpa.c03jsp.model;

import java.io.Serializable;

/**
 * Trida reprezentujici hrace. Odpovida zasadam pro JavaBean.
 *
 * @author Tomas Kadlec
 */
public class Player implements Serializable {

    private String name;
    private int score;
    private boolean active;

    /**
     * Konstruktor bez parametru
     */
    public Player() {
        this.active = true;
    }

    /**
     * setter pro name
     * @param name - name hrace
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter jmena
     * @return name hrace
     */
    public String getName() {
        return name;
    }

    /**
     * Setter, pokud uz mame primo cislo
     * @param score - score jako int
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Getter
     * @return hodnotu score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Zmeni stav hrace
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Indikace, zda hrac aktivne hraje
     * @return true pokud je hrac active, false jinak
     */
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String toString() {
        return "<tr><td>*" + this.name + "*</td><td>" + this.score + "</td></tr>";
    }
}
