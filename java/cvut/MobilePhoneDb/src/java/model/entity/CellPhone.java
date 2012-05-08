package model.entity;

/**
 * Trida je jeden zaznam - jeden telefon v databazi
 * @author Tomas Kadlec <kadleto2@fit.cvut.cz>
 */
public class CellPhone extends Device  {

    /**
     * Doba provozu ve stand by rezimu
     */
    protected int batteryLifeStandby;
    /**
     * Doba provozu pri hovoru
     */
    protected int batteryLifeCall;
    /**
     * Hmotnost
     */
    protected int weight;

    /**
     * Inicializace id
     */
    public CellPhone() {
        super();
    }
    
    /**
     * Inicializace id, vyrobce a typu
     * @param manuacturer
     * @param type 
     */
    public CellPhone(Manufacturer manufacturer, String type) {
        super(manufacturer, type);
    }

    /**
     * Inicialilzace vseho krome tagu
     * @param manufacturer - vyrobce
     * @param type - typ
     * @param batteryLifeStandby - vydrz stand by
     * @param batteryLifeCall - vydrz hovor
     * @param weight - hmotnost
     */
    public CellPhone(Manufacturer manufacturer, String type, int batteryLifeStandby, int batteryLifeCall, int weight) {
        super(manufacturer, type);
        this.manufacturer = manufacturer;
        this.type = type;
        this.batteryLifeStandby = batteryLifeStandby;
        this.batteryLifeCall = batteryLifeCall;
        this.weight = weight;
    }

    /* ** gettery a settery ** */
    
    public int getBatteryLifeCall() {
        return batteryLifeCall;
    }

    public void setBatteryLifeCall(int batteryLifeCall) {
        this.batteryLifeCall = batteryLifeCall;
    }

    public int getBatteryLifeStandby() {
        return batteryLifeStandby;
    }

    public void setBatteryLifeStandby(int batteryLifeStandby) {
        this.batteryLifeStandby = batteryLifeStandby;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
}
