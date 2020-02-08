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
 * Backing bean to support the profile editing page.
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

    /**
     * This is required by the @ManagedProperty to set the actual instance.
     * @param security
     */
    public void setSecurity(SecurityBean security) {
        this.security = security;
        this.currentUser = security.getLoggedInUser();
    }

    /**
     * Gets the current user that is being edited in the profile page.
     * @return the user entity instance.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Updates the user based on the profile form content.
     * @return the JSF page flow control string:
     *         success - if the user update wen't successfully
     *         failed - if the user update failed due to a supposedly validation error
     *         error - if the user update failed for unknown reason
     */
    public String updateUser() {
        try {
            if ((oldPassword != null) && !oldPassword.equals("")) {
                if (users.validatePassword(currentUser, oldPassword)) {
                    currentUser.setPassword(newPassword);
                    users.update(currentUser, true);
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old password incorrect", "You must input the old password correctly to change it to new one.");
                    FacesContext.getCurrentInstance().addMessage("profileForm:oldPassword", msg);
                    oldPassword = "";
                    newPassword = "";
                    return "failed";
                }
            } else {
                users.update(currentUser);
            }
        } catch (Exception ex) {
            return "error";
        }

        FacesMessage msg = new FacesMessage("Profile update successfully.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "success";
    }

    /**
     * Gets the new password (the password to replace the old password) of the
     * current user.
     * @return the new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password (the password to replace the old password) of the
     * current user.
     * @param password the new password
     */
    public void setNewPassword(String password) {
        this.newPassword = password;
    }

    /**
     * Gets the old password (the password to be replaced by the new password) of
     * the current user. This password is going to be verified and must match the
     * current password upon the updateUser call.
     * @return the old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets the old password (the password to be replaced by the new password) of
     * the current user. This password is going to be verified and must match the
     * current password upon the updateUser call.
     * @param oldPassword the old password
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Validates that the new password matches it's "again" field to make sure the
     * user has not made any typo.
     * @param context
     * @param toValidate
     * @param value
     */
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
