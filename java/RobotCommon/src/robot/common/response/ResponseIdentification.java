/*
 * ResponseIdentification
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

package robot.common.response;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseIdentification extends Response {

    protected String robotName;
    protected static final String idFormatString = "Ahoj kliente! Oslovuj mne %s.";

    public ResponseIdentification(String robotName) {
        this.robotName = robotName;
    }

    public ResponseIdentification() {
        this("");
    }

    public String formatForTcp() {
        return new StringBuilder("220 ").append(String.format(idFormatString, getRobotName())).append("\r\n").toString();
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    @Override
    public boolean isEndGame() {
        return false;
    }

}
