/*
 * InfoMessage.java
 */

package jsf.util;

import javax.faces.application.FacesMessage;

/**
 * Trida reprezentuje parametrizovanou informativni zpravu.
 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
public class InfoParametrizedMessage extends ParametrizedMessage {

    //// -------------------- konstruktory  -------------------- ////

    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    public InfoParametrizedMessage() {
        super(FacesMessage.SEVERITY_INFO);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param bundle - jazykovy resource
     */
    public InfoParametrizedMessage(String bundle) {
        super(FacesMessage.SEVERITY_INFO, bundle);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    public InfoParametrizedMessage(String summaryBundle, String detailBundle) {
        super(FacesMessage.SEVERITY_INFO, summaryBundle, detailBundle);
    }

}
