/*
 * ResponseCannotPickUp
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

package cz.cvut.fel.psi.robot.common.response;

import cz.cvut.fel.psi.robot.common.exception.RobotException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseCannotPickUp extends Response {

    public String formatForTcp() {
        return new StringBuilder("550 ").append("NELZE ZVEDNOUT ZNACKU").append("\r\n").toString();
    }

    @Override
    public boolean isEndGame() {
        return true;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleCannotPickUp();
    }

}
