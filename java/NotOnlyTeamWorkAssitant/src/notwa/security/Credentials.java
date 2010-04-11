/*
 * Credentials
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package notwa.security;

/**
 * <code>Credentials</code> represents a login and password pair which should be
 * usually validated againts the database connection to validate the <code>User</code>
 * login.
 * <p>The instance of this class also contains a userId which has sense together with
 * the supplied login which has been validated.</p>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Credentials {
    private String login;
    private String password;
    private int userId;
    private boolean valid;

    /**
     * The sole constructor providing login and password pairing.
     * 
     * @param login The actual user login.
     * @param password The actual user password.
     */
    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
        this.valid = false;
    }

    /**
     * Gets the user login previously provided to the constructor.
     * This login should match to the uniqe login present in the underlying
     * database and should be then replaced by the actual user id.
     *
     * @return The login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Gets the user password previously provided to the constructor.
     * This password should match to the password assigned to the <code>User</code>
     * identyfied by the actual login.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user id which should be filled by the module which is responsible
     * by the <code>Credentials</code> validation.
     * <p>As soon as that module recognizes this <code>credentials</code to be valid,
     * it should lookup the assigned user id and fill the <code>userId</code> property
     * of this class.</p>
     *
     * @return The user id.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user id which should be filled by the module which is responsible
     * by the <code>Credentials</code> validation.
     * <p>As soon as that module recognizes this <code>credentials</code to be valid,
     * it should lookup the assigned user id and fill the <code>userId</code> property
     * of this class using this setter.</p>
     *
     * @param userId The user id.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Verifies whether this <code>Credentials</code> have been considered to be
     * valid during the validation process.
     *
     * @return  <code>true</code if this <code>Credentials</code> are valid,
     *          <code>false</code> otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets this <code>Credentials</code> to be considered as valid during the
     * validation process.
     *
     * @param valid <code>true</code if this <code>Credentials</code> are valid,
     *              <code>false</code> otherwise.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
