package model.entity;

import java.io.Serializable;

/**
 * Trida reprezentuje jednoho vyrobce.
 * 
 * @author Tomas Kadlec <kadleto2@fit.cvut.cz>
 */
public class Manufacturer implements Serializable {
    
    /**
     * identifikator
     */
    protected int id;
    /**
     * nazev vyrobce
     */
    protected String name;

    /**
     * Inicializace - urceni id
     * FIXME identifikator neni pravdepodobne diky zmenam equals a hashCode nutny
     */
    public Manufacturer() {
        this.id = this.hashCode();
    }

    /**
     * Inicialiace - urceni id a nastaveni nazvu vyrobce
     * @param name - nazev vyrobce
     */
    public Manufacturer(String name) {
        this();
        this.name = name;
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
        final Manufacturer other = (Manufacturer) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    /* ** gettery a settery ** */
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /* ** textovy vypis ** */

    /**
     * Textova podoba vyrobce
     * @return nazev vyrobce
     */
    @Override
    public String toString() {
        return "" + name ;
    }

}
