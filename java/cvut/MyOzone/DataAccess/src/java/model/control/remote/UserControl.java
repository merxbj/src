/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control.remote;

import javax.ejb.Remote;
import model.User;

/**
 *
 * @author merxbj
 */
@Remote
public interface UserControl {

    public User getUserByUsername(String username);

    public void update(User user);

    public void update(User user, boolean updatePassword);

    public void registerNewUser(User newUser);
}
