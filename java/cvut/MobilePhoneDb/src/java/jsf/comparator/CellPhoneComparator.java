/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.comparator;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import model.entity.Manufacturer;

/**
 * Trida zajistuje metody razeni pro kauz v p:datatable.
 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
@ManagedBean
@ApplicationScoped
public class CellPhoneComparator extends Comparator {

    public int cmpManufacturer(Object o1, Object o2) {
        Manufacturer m1 = (Manufacturer) o1;
        Manufacturer m2 = (Manufacturer) o2;
        if (m1 == null) {
            return -1;
        } else if (m2 == null) {
            return 1;
        } else if (m2 == m1) {
            return 0;
        } else {
            return this.cmpStrings(m1.getName(), m2.getName());
        }
    }
    
    public int cmpType(Object o1, Object o2) {
        return  this.cmpStrings(o1, o2);
    }
}
