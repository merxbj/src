/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import model.User;
import model.control.remote.UserControl;

/**
 * Backing bean for the registratio form
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class RegisterBean {

    @EJB
    UserControl users;

    private User newUser;

    public RegisterBean() {
        init();
    }

    private void init() {
        newUser = new User();
        clear();
    }

    private void clear() {
        newUser.setFirstName("");
        newUser.setLastName("");
        newUser.setPassword("");
    }

    /**
     * Gets the entity instance of a new user that is about to be registered
     * @return the user instance
     */
    public User getNewUser() {
        return newUser;
    }

    /**
     * Sets the entity instance of a new user that is about to be registered
     * @param newUser the new user instance
     */
    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    /**
     * Validates that the new password matches it's "again" field to make sure the
     * user has not made any typo.
     * @param context
     * @param toValidate
     * @param value
     */
    public void validateSamePasswords(FacesContext context, UIComponent toValidate, Object value) {
        UIInput passwordField = (UIInput) context.getViewRoot().findComponent("registerForm:password");
        if (passwordField == null) {
            throw new IllegalArgumentException(String.format("Unable to find component."));
        }
        String password = (String) passwordField.getValue();
        String confirmPassword = (String) value;

        if (!confirmPassword.equals(password)) {
          FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match!", "Passwords do not match!");
          throw new ValidatorException(message);
        }
    }

    /**
     * Registers the new user with the application
     * @return the JSF page flow control string:
     *         success - if the user registration wen't successfully
     *         failed - if the user registration failed due to a supposedly validation error
     *         error - if the user registeration failed for unknown reason
     */
    public String register() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (users.getUserByUsername(newUser.getUsername()) == null) {
                users.registerNewUser(newUser);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username not available.", "Requested username already taken by someone else.");
                fc.addMessage("registerForm:username", msg);
                clear();
                return "failed";
            }
        } catch (Exception ex) {
            fc.addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to register the user.", "An error occured while registering a new user."));
            clear();
            return "error";
        }

        return "success";
    }

}
