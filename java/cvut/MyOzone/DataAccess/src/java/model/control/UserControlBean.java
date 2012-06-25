/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control;

import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.Group;
import model.User;
import model.UserGroup;
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
    public void update(User user, boolean updatePassword) {
        user.setPassword(md5.secureString(user.getPassword()));
        update(user);
    }

    @Override
    public void registerNewUser(User newUser) {
        
        /**
         * Hash the password and persist the user without any group assignment first
         */
        newUser.setPassword(md5.secureString(newUser.getPassword()));
        em.persist(newUser);

        /**
         * Pull the user back from the database and assign him/her to a group
         */
        newUser = (User) em.createNamedQuery("User.findByUsername").setParameter("username", newUser.getUsername()).getResultList().get(0);
        newUser.setUserGroupCollection(new ArrayList<UserGroup>(1));
        UserGroup ug = new UserGroup(newUser.getUserId(), Group.Users);
        ug.setUser(newUser);
        ug.setUsername(newUser.getUsername());
        newUser.getUserGroupCollection().add(ug);

        /**
         * And persist
         */
        em.persist(newUser);
    }

    @Override
    public boolean validatePassword(User user, String password) {
        return user.getPassword().equals(md5.secureString(password));
    }

}
