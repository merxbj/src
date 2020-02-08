package model.entity;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Obecne zarizeni - vzdy ma vyrobce a typ
 * 
 * @author Tomas Kadlec <kadleto2@fit.cvut.cz>
 */
public abstract class Device implements Serializable {

    /**
     * Identifikator
     */
    protected int id;
    /**
     * Vyrobce
     */
    protected Manufacturer manufacturer;
    /**
     * Typ zarizeni
     */
    protected String type;
    /**
     * Nastavene stitky pro snazsi hledani
     */
    protected HashSet<Tag> tags;

    /**
     * Konstruktor musi vzdy urcit jedinecne id
     * FIXME identifikator neni pravdepodobne diky zmenam equals a hashCode nutny
     */
    public Device() {
        id = this.hashCode();
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
        final Device other = (Device) obj;
        if (this.manufacturer != other.manufacturer && (this.manufacturer == null || !this.manufacturer.equals(other.manufacturer))) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.manufacturer != null ? this.manufacturer.hashCode() : 0);
        hash = 43 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    /**
     * Konstruktor inicializuje id, vyrobce a typ
     * @param manufacturer - vyrobce
     * @param type - typ
     */
    public Device(Manufacturer manufacturer, String type) {
        this();
        this.manufacturer = manufacturer;
        this.type = type;
    }

    /* ** prace se stitky ** */
    /**
     * Prida stitek
     * @param tag 
     * @return true v pripade uspechu, false jinak
     */
    public boolean addTag(Tag tag) {
        return this.tags.add(tag);
    }

    /**
     * Odebere stitek
     * @param tag
     * @return true v pripade uspechu, false jinak
     */
    public boolean removeTag(Tag tag) {
        return this.tags.remove(tag);
    }

    /* ** gettery a settery ** */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public HashSet<Tag> getTags() {
        return tags;
    }

    public void setTags(HashSet<Tag> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /* ** textovy vypis ** */
    @Override
    public String toString() {
        return "" + manufacturer + " " + type + " (" + id + ")";
    }
}
