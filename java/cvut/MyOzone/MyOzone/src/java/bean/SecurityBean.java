/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import model.control.UserControl;
import java.security.Principal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.User;

/**
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class SecurityBean {

    @EJB
    UserControl users;

    /** Creates a new instance of SecurityBean */
    public boolean getLoggedIn() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        return (p == null);
    }

    public User getLoggedInUser() {
        if (getLoggedIn()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            Principal p = fc.getExternalContext().getUserPrincipal();
            return users.getUserByUsername("mexbik");
        }
        return null;
    }

}
