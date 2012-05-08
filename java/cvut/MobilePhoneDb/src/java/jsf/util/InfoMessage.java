/*
 * InfoMessage.java
 */

package jsf.util;

import javax.faces.application.FacesMessage;

/**
 * Trida reprezentuje informativni zpravu.
 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
public class InfoMessage extends Message {

    //// -------------------- konstruktory  -------------------- ////

    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    public InfoMessage() {
        super(FacesMessage.SEVERITY_INFO);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param bundle - jazykovy resource
     */
    public InfoMessage(String bundle) {
        super(FacesMessage.SEVERITY_INFO, bundle);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    public InfoMessage(String summaryBundle, String detailBundle) {
        super(FacesMessage.SEVERITY_INFO, summaryBundle, detailBundle);
    }

}
