/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fel.cvut.ad7b39wpa.validating;

import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author eTeR
 */
public class BirthDateValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        boolean valid = true;
        if (value == null) {
            valid = false;
        }
        else {
            Date date = (Date) value;
            Date today = new Date();
            if (date.compareTo(today) > 0) {
                valid = false;
            }
        }

        if (!valid) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid birth date!", "Invalid birth date has been provided.");
            throw new ValidatorException(message);
        }
    }

}
