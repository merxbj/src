/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import model.manager.DeviceManager;
import model.entity.Tag;
import model.entity.Manufacturer;
import java.io.Serializable;
import java.util.*;
import model.entity.Device;
import model.manager.CellPhoneManager;

/**
 * Fasada pro praci s databazi (tedy v tomto pripade je databazi primo tahle trida
 * ale typicky budeme operovat nad relacni db pres tridu s takovymto rozhranim)
 * @author tomas
 */
public class Application implements Serializable {

    protected DeviceManager cellPhones;
    
    protected HashSet<Manufacturer> manufacturers;
    protected HashSet<Tag> tags;

    /**
     * Inicializace a naplneni databaze
     */
    public Application() {
        manufacturers = new HashSet<Manufacturer>();
        tags = new HashSet<Tag>();
        cellPhones = new CellPhoneManager();
        //cars
    }
    
    public Application(boolean testMode) {
        manufacturers = new HashSet<Manufacturer>();
        tags = new HashSet<Tag>();
        cellPhones = new CellPhoneManager(this);
    }

    /* ** prace s vyrobci ** */
    
    /**
     * Prida vyrobce, pokud jeste neni v kolekci
     * @param manufacturer vyrobce k pridani
     * @return Vzdy vraci pridaneho vyrobce
     */
    public Manufacturer addManufacturer(Manufacturer manufacturer) {
        this.manufacturers.add(manufacturer);
        return manufacturer;
    }
    
    /**
     * Odebere vyrobce. 
     * 
     * FIXME Mela by se kontrolovat referencni integrita
     * 
     * @param manufacturer vyrobce k odebrani
     */
    public void removeManufacturer(Manufacturer manufacturer) {
        this.manufacturers.remove(manufacturer);
    }
    
    /**
     * Najde vyrobce a to bud prave jednoho (presne zadany nazev), nebo takove
     * jejichz nazev zacina podle parametru. Pokud nevyhovuje zadny, vraci prazdnou
     * kolekci
     * @param name nazev vyrobce nebo jeho zacatek (nerozlisuje velikost pismen)
     */
    public List<Manufacturer> findManufacturersBy(String name) {
        ArrayList<Manufacturer> result = new ArrayList<Manufacturer>();
        if (this.manufacturers.contains(new Manufacturer(name))) {
            result.add(new Manufacturer(name));
            return result;
        }
        else {
            for (Manufacturer manufacturer : manufacturers) {
                if (manufacturer.getName().toLowerCase().startsWith(name.toLowerCase())) {
                    result.add(manufacturer);
                }
            }
        }
        return result;
    }
    
    public List<Manufacturer> findManufacturers() {
        return new ArrayList<Manufacturer>(manufacturers);
    }
    
    /* ** prace s tagy ** */
    
    /**
     * Prida tag, pokud jeste neni v kolekci
     * @param tag tag k pridani
     * @return Vzdy vraci pridany tag
     */
    public Tag addTag(Tag tag) {
        this.tags.add(tag);
        return tag;
    }
    
    /**
     * Odebere tag. 
     * 
     * FIXME Mela by se kontrolovat zda tag nekdo nepouziva a odebrat jej
     * vsude
     * 
     * @param tag tag k odebrani
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    /**
     * Najde tag a to bud prave jeden (presne zadany nazev), nebo takove
     * jejichz nazev zacina podle parametru. Pokud nevyhovuje zadny, vraci prazdnou
     * kolekci
     * @param value nazev tagu nebo jeho zacatek (nerozlisuje velikost pismen)
     */
    public List<Tag> findTagsBy(String value) {
        ArrayList<Tag> result = new ArrayList<Tag>();
        if (this.tags.contains(new Tag(value))) {
            result.add(new Tag(value));
            return result;
        }
        else {
            for (Tag tag : tags) {
                if (tag.getValue().toLowerCase().startsWith(value.toLowerCase())) {
                    result.add(tag);
                }
            }
        }
        return result;
    }
    
    /**
     * Najde vsechny tagy
     * @return kolekce vsech tagu
     */
    public List<Tag> findTags() {
        return new ArrayList<Tag>(tags);
    }
    
    /* ** gettery a settery ** */

    public DeviceManager getCellPhones() {
        return cellPhones;
    }

    public void setCellPhones(DeviceManager cellPhones) {
        this.cellPhones = cellPhones;
    }
    
    /* ** testovani ** */
    
    public static void main(String[] args) {
        Application a = new Application(true);
        for (Device cellPhone : a.getCellPhones().findDevices()) {
            System.out.println("" + cellPhone);
        }
        
    }
    
}