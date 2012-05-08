/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import model.entity.Manufacturer;

/**
 *
 * @author eTeR
 */
@FacesConverter("manufacturerConverter")
public class ManufacturerConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if ((value == null) || value.isEmpty()) {
            return null;
        }
        return new Manufacturer(value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (!(value instanceof Manufacturer)) {
            return null;
        }
        return ((Manufacturer) value).getName();
    }

}
