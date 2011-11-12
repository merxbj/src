/*
 * RobotDamagedState
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

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotDamagedState implements RobotState {

    private int damagedBlock;

    public RobotDamagedState(int damagedBlock) {
        assert((damagedBlock > 0) && (damagedBlock < 10));
        this.damagedBlock = damagedBlock;
    }

    public void doStep(Robot robot) throws RobotCrumbledException {
        throw new RobotCrumbledException();
    }

    public void turnLeft(Robot robot) throws RobotBatteryEmptyException {
        robot.getInfo().turn();

        robot.getInfo().getBattery().level -= 10;
        if (robot.getInfo().getBattery().level <= 0) {
            throw new RobotBatteryEmptyException();
        }
    }

    public void repair(Robot robot, int blockToRepair) throws RobotNoDamageException {
        if (damagedBlock != blockToRepair) {
            throw new RobotNoDamageException();
        }
        robot.getInfo().setStepsSoFar(0);
        robot.setCurrentState(new RobotOkState());
    }

    public String pickUp(Robot robot) throws RobotCannotPickUpException {
        if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
            throw new RobotCannotPickUpException();
        }

        return robot.getSecretMessage();
    }

    public void recharge(Robot robot) throws RobotCrumbledException, RobotDamagedException {
        throw new RobotCrumbledException();
    }

}
