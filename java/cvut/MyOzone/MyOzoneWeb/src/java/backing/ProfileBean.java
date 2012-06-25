/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
public class ProfileBean {

    @ManagedProperty(value="#{securityBean}")
    SecurityBean security;

    @EJB
    UserControl users;

    private User currentUser;
    private String oldPassword;
    private String newPassword;

    public void setSecurity(SecurityBean security) {
        this.security = security;
        this.currentUser = security.getLoggedInUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String updateUser() {
        users.update(currentUser);
        return "success";
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String password) {
        this.newPassword = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void validateSamePasswords(FacesContext context, UIComponent toValidate, Object value) {
        
        UIInput oldPasswordField = (UIInput) context.getViewRoot().findComponent("profileForm:oldPassword");
        if (oldPasswordField == null) {
            throw new IllegalArgumentException(String.format("Unable to find component."));
        }

        String oldPasswordValue = (String) oldPasswordField.getValue();
        if ((oldPasswordValue == null) || oldPasswordValue.equals("")) {
            // user doesn't intent to change the password, skip the "must match validation"
            return;
        }

        UIInput newPasswordField = (UIInput) context.getViewRoot().findComponent("profileForm:newPassword");
        if (newPasswordField == null) {
            throw new IllegalArgumentException(String.format("Unable to find component."));
        }
        String newPasswordValue = (String) newPasswordField.getValue();
        String newPasswordAgainValue = (String) value;

        if (!newPasswordAgainValue.equals(newPasswordValue)) {
          FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match!", "Passwords do not match!");
          throw new ValidatorException(message);
        }
    }

}
