/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsf.util;

import javax.faces.application.FacesMessage;

/**
 * Parametrizovatelna chybova zprava
 * @author tomas
 */
public class ErrorParametrizedMessage extends ParametrizedMessage {

   //// -------------------- konstruktory  -------------------- ////

    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    public ErrorParametrizedMessage() {
        super(FacesMessage.SEVERITY_ERROR);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param bundle - jazykovy resource
     */
    public ErrorParametrizedMessage(String bundle) {
        super(FacesMessage.SEVERITY_ERROR, bundle);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    public ErrorParametrizedMessage(String summaryBundle, String detailBundle) {
        super(FacesMessage.SEVERITY_ERROR, summaryBundle, detailBundle);
    }
}
