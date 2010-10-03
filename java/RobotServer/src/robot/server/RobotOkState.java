/*
 * RobotOkState
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

package robot.server;

import robot.common.exception.*;
import robot.server.exception.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotOkState implements RobotState {

    public void doStep(Robot robot) throws RobotCrashedException, RobotBatteryEmptyException, RobotDamagedException {
        RobotStatus status = robot.getStatus();

        try {
            status.setX(status.getX() + status.getDir_x());
            status.setY(status.getY() + status.getDir_y());
            status.setStepsSoFar(status.getStepsSoFar() + 1);
        } catch (RobotOutOfFieldException ex) {
            throw new RobotCrashedException(ex);
        }

        status.setBattery(status.getBattery() - 10);
        if (status.getBattery() == 0) {
            throw new RobotBatteryEmptyException();
        }

        boolean robotDamaged = Math.ceil(Math.random() * 10) <= status.getStepsSoFar();
        if (robotDamaged) {
            int damagedBlock = (int) Math.ceil(Math.random() * 8) + 1;
            robot.setCurrentState(new RobotDamagedState(damagedBlock));
            throw new RobotDamagedException(damagedBlock);
        }


    }

    public void turnLeft(Robot robot) throws RobotBatteryEmptyException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
