/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control.remote;

import javax.ejb.Remote;
import model.User;

/**
 * Remote interface to simplify the access to the user entities.
 * @author merxbj
 */
@Remote
public interface UserControl {

    /**
     * Gets an user entity based on the given username.
     * @param username the username of an user to look for
     * @return user entity of the user with the given username if exists,
     *         null otherwise.
     */
    public User getUserByUsername(String username);

    /**
     * Updates the user information of the given user. The new data actually
     * are in the given user entity instance. The user will be identified by the
     * contained primary key.
     * @param user to be updated
     */
    public void update(User user);

    /**
     * Updates the user information of the given user. In addition optionally 
     * takes the password stored in the given user instance in plain-text, md5
     * it and then updates the user information.
     * @param user user to be updated
     * @param updatePassword determines whether the password should be updated too
     */
    public void update(User user, boolean updatePassword);

    /**
     * Registers a new user based on the given user entitity instance.
     * It is assumed that there is a password stored in that entity in the
     * plain-text form and will be hashed appropriately.
     * @param newUser the user to be registered
     */
    public void registerNewUser(User newUser);

    /**
     * Validates whether the given plain-text password is valid for the given
     * user. Basically it just hashes the given plain-text password and compares
     * the results.
     * @param user to validate the password for
     * @param password the plain-text password to validate
     * @return true if the given plain-text password is valid password for the
     *         given user.
     */
    public boolean validatePassword(User user, String password);
}
