/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import model.User;

/**
 *
 * @author eTeR
 */
@LocalBean
@Stateless
public class UserControl {

    @PersistenceUnit
    public EntityManagerFactory emf;

    public User getUserByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("User.findByUsername");
        q.setParameter("username", username);
        return (User) q.getSingleResult();
    }

    public void update(User user) {
        EntityManager em = emf.createEntityManager();
        em.merge(user);
    }

}
