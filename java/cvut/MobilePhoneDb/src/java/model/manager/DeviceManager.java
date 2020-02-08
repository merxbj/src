package model.manager;

import model.entity.Device;
import model.entity.Manufacturer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import model.Application;
import model.entity.Tag;

/**
 * Fasada pro praci s databazi, umoznuje pridavat a odebirat zaznamy a vyhledavat
 * v nich podle ruznych kriterii. 
 * 
 * Ulozeni dat je zatim reseno pouze v JSF a to bud v session nebo application
 * scope. Napojeni na databazi pomoci JPA bude realizovano na nekterem z nasledujicich
 * cviceni.
 * 
 * @author Tomas Kadlec <kadleto2@fit.cvut.cz>
 */
public abstract class DeviceManager implements Serializable {
    
    /**
     * uloziste, zatim neumime databazi ...
     */
    protected HashSet<Device> devices;
    
    /**
     * Aktualni instace aplikace
     */
    protected Application application;

    /**
     * inicializace
     */
    public DeviceManager() {
        this.devices = new HashSet<Device>();
    }
    
    /**
     * inicializace pro testovaci ucely
     * @param application aplikace
     */
    public DeviceManager(Application application) {
        this();
        this.application = application;
    }

    /**
     * Prida jedno zarizeni
     * @param device zarizeni
     * @return true v pripade uspechu, false jinak
     */
    public boolean addDevice(Device device) {
        return this.devices.add(device);
    }

    /**
     * Prida vice zarizeni
     * @param devices kolekce zarizeni
     * @return true v pripade uspechu, false jinak
     */
    public boolean addDevices(Collection<Device> devices) {
        return this.devices.addAll(devices);
    }
    
    /**
     * Odebere jedno zarizeni
     * @param device zarizeni
     * @return true v pripade uspechu, false jinak
     */
    public boolean removeDevice(Device device) {
        return this.devices.remove(device);
    }
    
    /**
     * Odebere vice zarizeni
     * @param devices kolekce zarizeni
     * @return true v pripade uspechu, false jinak
     */
    public boolean removeDevices(Collection<Device> devices) {
        return this.devices.removeAll(devices);
    }
    
    /**
     * Najde zarizeni podle jeho id.
     * @param id id zarizeni
     * @return nalezene zarizeni v pripade uspechu, jinak null
     */
    public Device findDeviceById(int id) {
        for (Device device : this.devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }
    
    /**
     * Najde vsechna zarizeni
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevices() {
        return new ArrayList<Device>(this.devices);
    }
    
    /**
     * Najde vsechna zarizeni daneho vyrobce
     * @param manufacturer vyrobce
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(Manufacturer manufacturer) {
        ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (device.getManufacturer() == manufacturer) {
                result.add(device);
            }
        }
        return result;
    }
    
    /**
     * Najde vsechna zarizeni daneho vyrobce a typu (zarizeni muze definovat treba nejaky podtyp)
     * @param manufacturer vyrobce
     * @param type typ
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(Manufacturer manufacturer, String type) {
    ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (device.getManufacturer() == manufacturer && device.getType().compareTo(type) == 0) {
                result.add(device);
            }
        }
        return result;
    }
    
    /**
     * Najde vsechna zarizeni daneho vyrobce a typu (zarizeni muze definovat treba nejaky podtyp) a tagu
     * @param manufacturer vyrobce
     * @param type typ
     * @param tags tagy
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(Manufacturer manufacturer, String type, Collection<Tag> tags) {
    ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (device.getManufacturer() == manufacturer && 
                    device.getType().compareTo(type) == 0 && 
                    tags.containsAll(device.getTags())) {
                result.add(device);
            }
        }
        return result;
    }

    /**
     * Najde vsechna zarizeni daneho typu (zarizeni muze definovat treba nejaky podtyp) a tagu
     * @param type typ
     * @param tags tagy
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(String type, Collection<Tag> tags) {
    ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (device.getType().compareTo(type) == 0 && 
                    tags.containsAll(device.getTags())) {
                result.add(device);
            }
        }
        return result;
    }
    
    /**
     * Najde vsechna zarizeni daneho typu (zarizeni muze definovat treba nejaky podtyp)
     * @param type typ
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(String type) {
    ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (device.getType().compareTo(type) == 0) {
                result.add(device);
            }
        }
        return result;
    }
    
    /**
     * Najde vsechna zarizeni danych tagu
     * @param tags tagy
     * @return nalezena zarizeni, vzdy vraci kolekci, ta ale muze byt prazdna!
     */
    public List<Device> findDevicesBy(Collection<Tag> tags) {
    ArrayList<Device> result = new ArrayList<Device>();
        for (Device device : this.devices) {
            if (tags.containsAll(device.getTags())) {
                result.add(device);
            }
        }
        return result;
    }
    
}
