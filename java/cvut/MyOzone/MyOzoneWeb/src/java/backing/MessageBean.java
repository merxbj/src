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
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class MessageBean {

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    public String getWelcomeMessage() {
        User user = security.getLoggedInUser();
        if (user != null) {
            return String.format("Welcome %s %s to MyOzone!", user.getFirstName(), user.getLastName());
        } else {
            return "Weclome to MyOzone!";
        }
    }

    public String getFooterMessage() {
        return "(C) by Jaroslav Merxbauer";
    }

    public void setSecurity(SecurityBean security) {
        this.security = security;
    }

}
