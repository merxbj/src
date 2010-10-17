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
        RobotServerInfo info = robot.getInfo();

        try {
            info.move();
            info.setStepsSoFar(info.getStepsSoFar() + 1);
        } catch (RobotOutOfFieldException ex) {
            throw new RobotCrashedException(ex);
        }

        info.setBattery(info.getBattery() - 10);
        if (info.getBattery() <= 0) {
            throw new RobotBatteryEmptyException();
        }

        boolean robotDamaged = Math.ceil(Math.random() * 10) <= (info.getStepsSoFar() % 10);
        if (robotDamaged) {
            int damagedBlock = damageRobot(robot);
            throw new RobotDamagedException(damagedBlock);
        }
    }

    public void turnLeft(Robot robot) throws RobotBatteryEmptyException {
        robot.getInfo().turn();

        robot.getInfo().setBattery(robot.getInfo().getBattery() - 10);
        if (robot.getInfo().getBattery() <= 0) {
            throw new RobotBatteryEmptyException();
        }
    }

    public void repair(Robot robot, int blockToRepair) throws RobotNoDamageException {
        throw new RobotNoDamageException();
    }

    public String pickUp(Robot robot) throws RobotCannotPickUpException {
        if (robot.getInfo().getX() != 0 || robot.getInfo().getY() != 0) {
            throw new RobotCannotPickUpException();
        }

        return robot.getSecretMessage();
    }

    public void recharge(Robot robot) throws RobotCrumbledException, RobotDamagedException {
        boolean robotDamaged = (Math.random() < 0.5);
        if (robotDamaged) {
            int damagedBlock = damageRobot(robot);
            robot.getInfo().setBattery(1);
            throw new RobotDamagedException(damagedBlock);
        }
        robot.getInfo().setBattery(100);
    }

    private int damageRobot(Robot robot) {
        int damagedBlock = (int) Math.ceil(Math.random() * 8) + 1;
        robot.setCurrentState(new RobotDamagedState(damagedBlock));
        return damagedBlock;
    }

}
