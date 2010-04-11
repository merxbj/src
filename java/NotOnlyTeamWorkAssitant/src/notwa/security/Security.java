/*
 * Security
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

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.SignInException;

/**
 * Security is the class providing simple task to maintain the application security.
 * It is currently implemented to be a singleton providing you an interface
 * to verify the validity of entered credentials to be allowed to access to the
 * WIT database.
 * <p>There are several security pitfals currently connected with the log-in
 * process:
 * <ul>
 * <li>It is not SQL insertion protected</li>
 * <li>Password isn't any encrypted</li>
 * </ul>
 * </p>
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Security {
    private static Security instance;
    
    /**
     * Gets the sole instance of the Security class which is maintained as the
     * static singleton.
     *
     * @return The singleton instance
     */
    public static Security getInstance() {
        if (instance == null) {
            instance = new Security();
        }
        return instance;
    }
    
    /**
     * Checks the given <code>Credentials</code> to be valid againts the given
     * <code>ConnectionInfo</code>. If yes, the actual user id is will be supplied
     * within the given <code>Credentials</code> instance.
     * 
     * @param ci The <code>ConnectionInfo</code> againts the given credetials should be validated
     * @param credentials The actual <code>Credentials</code> to be validated.
     *
     * @throws SignInException When invalid credentials have been supplied.
     */
    public void signIn(ConnectionInfo ci, Credentials credentials) throws SignInException {
        UserDal ud = new UserDal(ci, null);

        if (!ud.validateCredentials(credentials)) {
            throw new SignInException("Invalid login or password provided.");
        }
    }
}
