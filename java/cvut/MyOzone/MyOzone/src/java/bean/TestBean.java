/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import model.Accountable;
import model.User;

/**
 *
 * @author eTeR
 */
@ManagedBean
@SessionScoped
public class TestBean {

    @PersistenceUnit
    public EntityManagerFactory emf;

    public TestBean() {
    }

    public String getUser() {
        EntityManager em = emf.createEntityManager();
        User user = (User) em.createNamedQuery("User.findAll").getResultList().get(0);
        return user.getFirstName() + " " + user.getLastName();
    }

    public List<User> getUsers() {
        EntityManager em = emf.createEntityManager();
        List<User> users = (List<User>) em.createNamedQuery("User.findAll").getResultList();
        return users;
    }

    public List<Accountable> getAccountables() {
        EntityManager em = emf.createEntityManager();
        List<Accountable> accountables = (List<Accountable>) em.createNamedQuery("Accountable.findAll").getResultList();
        return accountables;
    }

}
