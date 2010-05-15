/*
 * ConnectionInfo
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

package notwa.common;

/**
 * Class encapsulating all the application settings keeped by the config file.
 * These settings could be obtained by the {@link Config#getApplicationSettings()} and
 * changes to it will be promoted as soon as
 * {@link Config#setApplicationsSettings(notwa.common.ApplicationSettings)} are called.
 *
 *
 * @author  Tomas Studnicka
 * @version %I% %G%
 */
public class ApplicationSettings {
    private String skin;
    
    /**
     * Sets the configured Look & Feel skin class name.
     * 
     * @param skin
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }
    
    /**
     * Gets the configured Look & Feel skin class name.
     * 
     * @return skin
     */
    public String getSkin() {
        return skin;
    }
}
