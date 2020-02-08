/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.controller;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.entity.CellPhone;
import model.manager.CellPhoneManager;
import jsf.util.InfoMessage;
import model.Application;
import model.entity.Device;
import model.entity.Manufacturer;

/**
 * Trida je takovy "action controller" - poskytuje interface k business logice,
 * v tomhle pripade k databazi telefonu
 *
 * Databaze neni trvale ulozena a ma zivonost stejnou jako tenhle objekt - session scope!
 * @author tomas
 */
@ManagedBean(name = "cellPhone")
@SessionScoped
public class CellPhoneController {

    /**
     * Instance fasady pro pristup k databazi
     */
    protected CellPhoneManager cpm;
    /**
     * Instance telefonu pro formular
     */
    protected CellPhone phone;
    /**
     * Instance telefonu pro vyber
     */
    protected CellPhone selectedPhone;
    /**
     * Instance aplikace 
     */
    protected Application application;

    /**
     * Vytvari novou instanci - inicializace
     */
    public CellPhoneController() {
        application = new Application(true);
        cpm = (CellPhoneManager) application.getCellPhones();
        phone = new CellPhone();
        selectedPhone = new CellPhone();
    }

    /**
     * Vrati kolekci vsech telefonu z databaze
     * @return kolekce telefonu
     */
    public List<Device> getPhones() {
        return (List<Device>) cpm.findDevices();
    }

    /**
     * Action listener pro akci pridat telefon
     * @return navigacni retezec jsf
     */
    public String addPhone() {
        cpm.addDevice(phone);
        application.addManufacturer(phone.getManufacturer());
        phone = new CellPhone();
        new InfoMessage("appBundle").setSummary("msg.success").add();
        return "index";
    }

    public CellPhone getPhone() {
        return phone;
    }

    public void setPhone(CellPhone phone) {
        this.phone = phone;
    }

    public CellPhone getSelectedPhone() {
        return selectedPhone;
    }

    public void setSelectedPhone(CellPhone selectedPhone) {
        this.selectedPhone = selectedPhone;
    }

    public List<Manufacturer> getManufacturers(String query) {
        return application.findManufacturersBy(query);
    }
    
}