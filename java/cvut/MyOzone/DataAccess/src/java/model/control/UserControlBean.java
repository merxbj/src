/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.User;
import model.control.remote.UserControl;
import model.utility.Md5Hasher;

/**
 *
 * @author eTeR
 */
@Stateless
public class UserControlBean implements UserControl {

    @PersistenceContext
    public EntityManager em;

    Md5Hasher md5;

    public UserControlBean() {
         md5 = new Md5Hasher();
    }

    @Override
    public User getUserByUsername(String username) {
        Query q = em.createNamedQuery("User.findByUsername");
        q.setParameter("username", username);
        return (User) q.getSingleResult();
    }

    @Override
    public void update(User user) {
        em.merge(user);
    }

    @Override
    public void registerNewUser(User newUser) {
        newUser.setPassword(md5.secureString(newUser.getPassword()));
        em.persist(newUser);
    }

}
