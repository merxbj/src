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

package cz.cvut.fel.psi.robot.server;

import cz.cvut.fel.psi.robot.common.exception.*;
import cz.cvut.fel.psi.robot.server.exception.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotOkState implements RobotState {

    public void doStep(Robot robot) throws RobotCrashedException, RobotProcessorDamagedException {
        RobotServerInfo info = robot.getInfo();

        boolean robotDamaged = Math.ceil(Math.random() * 10) <= (info.getStepsSoFar() % 10);
        if (robotDamaged) {
            int damagedProcessor = damageRobotProcessor(robot);
            throw new RobotProcessorDamagedException(damagedProcessor);
        }

        try {
            info.move();
            info.setStepsSoFar(info.getStepsSoFar() + 1);
        } catch (RobotOutOfFieldException ex) {
            throw new RobotCrashedException(ex);
        }
    }

    public void turnLeft(Robot robot) {
        robot.getInfo().turn();
    }

    public void repair(Robot robot, int processorToRepair) throws RobotProcessorOkException {
        throw new RobotProcessorOkException();
    }

    public String pickUp(Robot robot) throws RobotCannotPickUpException {
        if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
            throw new RobotCannotPickUpException();
        }

        return robot.getSecretMessage();
    }

    private int damageRobotProcessor(Robot robot) {
        int damagedProcessor = (int) Math.ceil(Math.random() * 8) + 1;
        robot.setCurrentState(new RobotProcessorDamagedState(damagedProcessor));
        return damagedProcessor;
    }

}
