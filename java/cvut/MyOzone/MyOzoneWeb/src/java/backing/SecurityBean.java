/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import model.control.remote.UserControl;
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
 * Not only a backing bean for the login applet but also an utility bean to
 * provide a convenient interface to security means of the application
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

    /**
     * Finds out if there is an user currently logged in to the application.
     * @return true, if there is an user logged in, false otherwise
     */
    public boolean getLoggedIn() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        return (p != null);
    }

    /**
     * Gets the user entity instance of the user that is currently logged in
     * (if any), null otherwise
     * @return
     */
    public User getLoggedInUser() {
        if (getLoggedIn()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            Principal p = fc.getExternalContext().getUserPrincipal();
            return users.getUserByUsername(p.getName());
        }
        return null;
    }

    /**
     * Backing support for the login applet. Gets the user entity instance of
     * the user that is currently in the login process
     * @return a user that is currently logging in
     */
    public User getLoggingUser() {
        return loggingUser;
    }

    /**
     * Backing support for the login applet. Sets the user entity instance of
     * the user that is currently in the login process
     * @param loggingUser a user that is currently logging in
     */
    public void setLoggingUser(User loggingUser) {
        this.loggingUser = loggingUser;
    }

    /**
     * Performs the actual login operation (programmatic login - security managed
     * by the container)
     * @return the JSF page flow control string:
     *         success - if the user log in wen't successfully
     *         failed - if the user log in failed (invalid username/password)
     */
    public String login() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        try {
            request.login(loggingUser.getUsername(), loggingUser.getPassword());
        } catch (ServletException ex) {
            fc.addMessage("loginForm:password", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed.", "Login failed."));
            loggingUser.setPassword("");
            return "failed";
        }
        loggingUser = new User();
        return "success";
    }

}
