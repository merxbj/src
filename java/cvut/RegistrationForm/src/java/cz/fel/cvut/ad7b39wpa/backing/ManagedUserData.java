/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fel.cvut.ad7b39wpa.backing;

import java.util.Date;
import javax.faces.model.SelectItem;

/**
 *
 * @author eTeR
 */
public class ManagedUserData {

    private String firstName;
    private String lastName;
    private Date birthDate;
    private Sex sex;

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public enum Sex {
        Male("Male"), Female("Female");

        private final String label;

        private Sex(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public SelectItem[] getSexes() {
        SelectItem[] items = new SelectItem[Sex.values().length];
        int i = 0;
        for (Sex g : Sex.values()) {
            items[i++] = new SelectItem(g, g.getLabel());
        }
        return items;
    }

}
