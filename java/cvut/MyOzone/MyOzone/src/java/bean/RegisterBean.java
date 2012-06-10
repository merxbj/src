/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import model.User;
import model.control.UserControl;

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

    public void validateSamePassword(FacesContext context, UIComponent toValidate, Object value) {
        String confirmPassword = (String) value;
        if (!confirmPassword.equals(newUser.getPassword())) {
          FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match!", "Passwords do not match!");
          throw new ValidatorException(message);
        }
    }

    public String register() {
        try {
            users.registerNewUser(newUser);
            newUser = new User();
        } catch (Exception ex) {
            return "error";
        }

        return "success";
    }

}
