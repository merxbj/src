/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsf.util;

import javax.faces.application.FacesMessage;

/**
 * Chybova zprava
 * @author tomas
 */
public class ErrorMessage extends Message {

   //// -------------------- konstruktory  -------------------- ////

    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    public ErrorMessage() {
        super(FacesMessage.SEVERITY_ERROR);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param bundle - jazykovy resource
     */
    public ErrorMessage(String bundle) {
        super(FacesMessage.SEVERITY_ERROR, bundle);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    public ErrorMessage(String summaryBundle, String detailBundle) {
        super(FacesMessage.SEVERITY_ERROR, summaryBundle, detailBundle);
    }

}
