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
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class RegisterBean {

    @EJB
    UserControl users;

    private User newUser;

    /** Creates a new instance of RegisterBean */
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

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

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
