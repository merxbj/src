/*
 * RobotCannotPickUpException
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

package cz.cvut.fel.psi.robot.common.exception;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotCannotPickUpException extends RobotException {

    public RobotCannotPickUpException(Throwable cause) {
        super(cause);
    }

    public RobotCannotPickUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public RobotCannotPickUpException(String message) {
        super(message);
    }

    public RobotCannotPickUpException() {
    }

}
