/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.util;

import java.text.MessageFormat;
import javax.faces.application.FacesMessage.Severity;

/**
 * Umoznuje dopnit informace do prekladovych retezcu, takova mista jsou oznacena
 * {1}, {2}, ... Pro nahrady se pouziva trida java.text.MessageFormat.

 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
public class ParametrizedMessage extends Message {

    protected Object[] values;

    //// ------------------ konstruktory ------------------ ////

    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    protected ParametrizedMessage(Severity severity) {
        super(severity);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param bundle - jazykovy resource
     */
    protected ParametrizedMessage(Severity severity, String bundle) {
        super(severity, bundle);
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    protected ParametrizedMessage(Severity severity, String summaryBundle, String detailBundle) {
        super(severity, summaryBundle, detailBundle);
    }


    //// ------------------ nastaveni hodnot ------------------ ////

    /**
     * Nastavi hodnoty, ktere se maji nahradit. Mely by mit vsechny rozume implementovanou
     * metodu toString()!
     * @param values - hodnoty (vararg)
     * @return aktualni instanci (fluent API)
     */
    public Message setValues(Object... values) {
        this.values = values;
        return this;
    }

    //// ------------------ nahrazeni parametru ------------------ ////

    /**
     * Metoda nahrazuje identifikator retezce vlastnim retezcem, pokud existuje bundle.
     * V prubehu vypoctu jsou nahrazeny i zastupne symboly {1}, {2} ...
     * @param bundle - bundle
     * @param message - identifikator nebo zprava
     * @return vysledny retezec
     */
    @Override
    protected String getText(String bundle, String message) {
        // pokud je zadan bundle, najdu zpravu, jinak se pouzije primo zadana
        if (bundle != null && !bundle.isEmpty()) {
            message = getLocalizedMessage(bundle, message);
        }
        // a zkusim pomoci MessageFormat provest nahrady, kdyz se to nepovede, vracim puvodni retezec
        try {
            return MessageFormat.format(message, values);
        } catch (IllegalArgumentException e) {
            return message;
        }

    }

}
