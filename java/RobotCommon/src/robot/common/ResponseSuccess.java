/*
 * ResponseSuccess
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

package robot.common;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseSuccess extends Response {

    protected String secretString;

    public ResponseSuccess(String secretString) {
        this.secretString = secretString;
    }

    public ResponseSuccess() {
        this("Neinicializovane tajemstvi - programator si nepral zadne sdelit!");
    }

    public String formatForTcp() {
        return new StringBuilder("221 ").append("USPECH ").append(secretString).append("\r\n").toString();
    }

    public String getSecretString() {
        return secretString;
    }

    public void setSecretString(String secretString) {
        this.secretString = secretString;
    }

    @Override
    public boolean isEndGame() {
        return true;
    }

}
