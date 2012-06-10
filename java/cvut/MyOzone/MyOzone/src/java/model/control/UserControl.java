/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.User;
import model.UserGroup;
import model.utility.Md5Hasher;

/**
 *
 * @author eTeR
 */
@LocalBean
@Stateless
public class UserControl {

    @PersistenceContext
    public EntityManager em;

    Md5Hasher md5;

    public UserControl() {
         md5 = new Md5Hasher();
    }

    public User getUserByUsername(String username) {
        Query q = em.createNamedQuery("User.findByUsername");
        q.setParameter("username", username);
        return (User) q.getSingleResult();
    }

    public void update(User user) {
        em.merge(user);
    }

    public void registerNewUser(User newUser) {
        newUser.setPassword(md5.secureString(newUser.getPassword()));
        em.persist(newUser);
    }

}
