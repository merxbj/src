/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import model.User;

/**
 * Very simple backing bean to provide some of the messages.
 * Originally this was just to test the functionality but I ended up with liking
 * it's simplicity so I won't be removing it.
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class MessageBean {

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    /**
     * Gets the header welcome message based on the logged in user (if any)
     * @return the welcome message
     */
    public String getWelcomeMessage() {
        User user = security.getLoggedInUser();
        if (user != null) {
            return String.format("Welcome %s %s to MyOzone!", user.getFirstName(), user.getLastName());
        } else {
            return "Weclome to MyOzone!";
        }
    }

    /**
     * Gets the footer message.
     * @return the footer message
     */
    public String getFooterMessage() {
        return "(C) by Jaroslav Merxbauer";
    }

    /**
     * This is required by @ManagedProperty to set the actual instance.
     * @param security
     */
    public void setSecurity(SecurityBean security) {
        this.security = security;
    }

}
