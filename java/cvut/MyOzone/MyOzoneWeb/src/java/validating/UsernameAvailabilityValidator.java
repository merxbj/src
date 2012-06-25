/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package validating;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import model.control.remote.UserControl;

/**
 *
 * @author eTeR
 */
public class UsernameAvailabilityValidator implements Validator {

    @EJB
    UserControl users;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        boolean valid = true;
        if ((value == null) || !(value instanceof String)) {
            valid = false;
        } else {
            valid = (users.getUserByUsername((String) value) == null);
        }

        if (!valid) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username not available.", "Requested username already taken by someone else.");
            throw new ValidatorException(message);
        }
    }

}
