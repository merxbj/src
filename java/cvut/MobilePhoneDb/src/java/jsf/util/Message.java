/*
 * Message.java
 */
package jsf.util;

import java.util.MissingResourceException;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Trida umoznuje zakladni praci s lokalizovanymi texty
 * a zpravami pro JSF
 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
public class Message {

    protected Severity severity;
    protected String summary;
    protected String summaryBundle;
    protected String detail;
    protected String detailBundle;
    protected String component;

    //// ------------------ konstruktory ------------------ ////
    /**
     * Vytvori novou prazdnou zpravu, neni nastaven ani typ!
     */
    protected Message(Severity severity) {
        this.severity = severity;
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * spolecnym pro summary i detail.
     * @param severity - typ zpravy
     * @param bundle - jazykovy resource
     */
    protected Message(Severity severity, String bundle) {
        this(severity);
        this.summaryBundle = this.detailBundle = bundle;
    }

    /**
     * Vytvori novou prazdnou zpravu s prednastavenym bundlem
     * pro summary a detail.
     * @param severity - typ zpravy
     * @param summaryBundle - bundle pro summary
     * @param detailBundle - bundle pro detail
     */
    protected Message(Severity severity, String summaryBundle, String detailBundle) {
        this(severity);
        this.summaryBundle = summaryBundle;
        this.detailBundle = detailBundle;
    }

    //// ------------------ settery ------------------ ////
    /**
     * Nastavi zpravu komponente
     * @param component - komponenta
     * @return aktualni instanci (fluent API)
     */
    public Message setComponent(String component) {
        this.component = component;
        return this;
    }

    /**
     * Nastavi zpravu komponente
     * @param component - komponenta
     * @return aktualni instanci (fluent API)
     */
    public Message setComponent(UIComponent component) {
        this.component = component.getClientId(FacesContext.getCurrentInstance());
        return this;
    }

    /**
     * Nastavi shrnuti/nadpis zpravy.
     * @param summary - shrnuti
     * @return aktualni instanci (fluent API)
     */
    public Message setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    /**
     * Nastavi bundle, ze ktereho se ma vzit zprava.
     * @param summaryBundle - bundle
     * @return aktualni instanci (fluent API)
     */
    public Message setSummaryBundle(String summaryBundle) {
        this.summaryBundle = summaryBundle;
        return this;
    }

    /**
     * Nastavi podrobny popis.
     * @param detail - popis
     * @return aktualni instanci (fluent API)
     */
    public Message setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * Nastavi bundle, ze ktereho se ma vzit zprava.
     * @param detailBundle - bundle
     * @return aktualni instanci (fluent API)
     */
    public Message setDetailBundle(String detailBundle) {
        this.detailBundle = detailBundle;
        return this;
    }

    //// ------------------ pridani zpravy ------------------ ////
    /**
     * Prida chybovou zpravu podle nastavenych hodnot konstruktorem a
     * metodami set*
     */
    public void add() {
        // vytvoreni zpravy
        FacesMessage facesMsg = new FacesMessage();
        facesMsg.setSeverity(severity);
        facesMsg.setSummary(this.getText(summaryBundle, summary));
        facesMsg.setDetail(this.getText(detailBundle, detail));
        // pridani zpravy
        FacesContext.getCurrentInstance().addMessage(component, facesMsg);
    }

    /**
     * Servisni metoda, ktera se pokusi dohledat prekladovy retezec v bundlu. Pokud
     * bundle je null nebo prazdny, jako preklad se bere predana zprava.
     * @param bundle - zvoleny bundle
     * @param message - zprava k prelozeni
     * @return - prelozena zprava, pokud je bundle != null, a !isEmpty(), jinak message
     */
    protected String getText(String bundle, String message) {
        if (message == null || message.isEmpty() || bundle == null || bundle.isEmpty()) {
            return null;
        } else {
            return getLocalizedMessage(bundle, message);
        }
    }

    //// ------------------ staticke metody ------------------ ////
    /**
     * Pristup k lokalizovanym zpravam
     * @param bundle - nazev budle pro EL (jak je definovany ve faces-config.xml)
     * @param message - identifikator zpravy
     * @return lokalizovanou zpravu
     */
    public static String getLocalizedMessage(String bundle, String message) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            return context.getApplication().getResourceBundle(context, bundle).getString(message);
        } catch (MissingResourceException e) {
            return "???" + e.getKey() + "???";
        } catch (NullPointerException e) {
            return "!!!" + message + "!!!";
        }
    }
}
