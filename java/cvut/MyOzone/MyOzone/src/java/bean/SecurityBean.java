/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import model.control.UserControl;
import java.security.Principal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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

    private User loggingUser;

    public SecurityBean() {
        loggingUser = new User();
    }

    /** Creates a new instance of SecurityBean */
    public boolean getLoggedIn() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        return (p != null);
    }

    public User getLoggedInUser() {
        if (getLoggedIn()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            Principal p = fc.getExternalContext().getUserPrincipal();
            return users.getUserByUsername(p.getName());
        }
        return null;
    }

    public User getLoggingUser() {
        return loggingUser;
    }

    public void setLoggingUser(User loggingUser) {
        this.loggingUser = loggingUser;
    }

    public String login() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        try {
            request.login(loggingUser.getUsername(), loggingUser.getPassword());
        } catch (ServletException ex) {
            fc.addMessage("loginForm:password", new FacesMessage("Login failed."));
            loggingUser.setPassword("");
            return "error";
        }
        loggingUser = new User();
        return "success";
    }

}
