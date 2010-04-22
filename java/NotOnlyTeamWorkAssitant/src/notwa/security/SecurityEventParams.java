/*
 * SecurityEventParams
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
import notwa.common.EventParams;

/**
 * The parameters coming together with the {@link SecurityEvent}.
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SecurityEventParams extends EventParams {
    
    /**
     * Event informing its handler about the successful state of the login process.
     */
    public static final int SECURITY_EVENT_SUCCESSFUL_LOGIN = 1;
    
    private Credentials credentials;
    private ConnectionInfo connectionInfo;

    /**
     * The sole constructor providing the actual event identification as well as the
     * credentials and connection info to be carried together with the event.
     * <p>The credentials and connection info are usually those parameters which
     * compiles the security handling that has been done before this event has
     * been fired.</p>
     *
     * @param eventId The event identifier.
     * @param credentials The user credetials.
     * @param connectionInfo The connection info to the secure database.
     */
    public SecurityEventParams(int eventId, Credentials credentials, ConnectionInfo connectionInfo) {
        super(eventId);
        this.credentials = credentials;
        this.connectionInfo = connectionInfo;
    }

    /**
     * Gets the connection info against which the security operation has been done.
     * 
     * @return The <code>ConnectionInfo</code>
     */
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    /**
     * Gets the user credentials the security operation has been done with.
     *
     * @return The <code>Credentials</code>
     */
    public Credentials getCredentials() {
        return credentials;
    }

}
