/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import java.io.Serializable;

/**
 *
 * @author tomas
 */
public class Tag implements Serializable {

    /**
     * Identifikator
     */
    protected int id;
    
    /**
     * Hodnota stitku
     */
    protected String value;

    /**
     * Konstruktor musi vzdy urcit jedinecne id
     * FIXME identifikator neni pravdepodobne diky zmenam equals a hashCode nutny
     */
    public Tag() {
        id = this.hashCode();
    }

    /**
     * Konstruktor nastavi hodnotu stitku a inicializuje id 
     * @param value  - hodnota stitku
     */
    public Tag(String value) {
        this();
        this.value = value;
    }

    /* ** Porovnani - zalezi na stavu ne na umisteni v pameti ** */
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    /* ** gettery a settery ** */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }    
    
}
