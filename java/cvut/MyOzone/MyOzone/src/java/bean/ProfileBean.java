/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import model.User;
import model.control.UserControl;

/**
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class ProfileBean {

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    @EJB
    UserControl users;

    private User currentUser;

    public void setSecurity(SecurityBean security) {
        this.security = security;
        this.currentUser = security.getLoggedInUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String updateUser() {
        users.update(currentUser);
        return "profile";
    }

}
