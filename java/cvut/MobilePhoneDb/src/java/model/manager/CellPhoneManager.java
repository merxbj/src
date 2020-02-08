/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.manager;

import model.Application;
import model.entity.CellPhone;
import model.entity.Manufacturer;

/**
 * Fasada pro praci s databazi (tedy v tomto pripade je databazi primo tahle trida
 * ale typicky budeme operovat nad relacni db pres tridu s takovymto rozhranim)
 * @author tomas
 */
public class CellPhoneManager extends DeviceManager {

    /**
     * Inicializace
     */
    public CellPhoneManager() {
        super();
    }
    
    /**
     * Inicializace s testovacimi daty
     * @param testMode - na hodnote nezalezi, pouze indikuje testovaci rezim
     */
    public CellPhoneManager(Application application) {
        super(application);
        this.devices.add(new CellPhone(application.addManufacturer(new Manufacturer("Samsung")), "550", 220, 8, 110));
        this.devices.add(new CellPhone(application.addManufacturer(new Manufacturer("Samsung")), "551", 320, 10, 140));
    }

}