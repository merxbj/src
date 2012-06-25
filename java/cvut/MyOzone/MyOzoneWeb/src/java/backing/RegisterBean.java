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
        newUser = new User();
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
        try {
            users.registerNewUser(newUser);
            newUser = new User();
        } catch (Exception ex) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage("registerForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to register the user.", "An error occured while registering a new user."));
            newUser.setPassword(null);
            return "failed";
        }

        return "success";
    }

}
