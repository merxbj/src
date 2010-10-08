/*
 * ClientRequestProcessor
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

import robot.common.request.RequestProcessor;
import robot.common.response.*;
import robot.common.exception.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ClientRequestProcessor implements RequestProcessor {

    private Robot robot;

    public ClientRequestProcessor(Robot robot) {
        this.robot = robot;
    }

    public Response processPickUp() {
        try {
            String secretMessage = robot.pickUp();
            return new ResponseSuccess(secretMessage);
        } catch (RobotCannotPickUpException ex) {
            return new ResponseCannotPickUp();
        }
    }

    public Response processRecharge() {
        try {
            RobotStatus status = robot.recharge();
            return new ResponseOk(status.getBattery(), status.getX(), status.getY());
        } catch (RobotCrumbledException ex) {
            return new ResponseCrumbled();
        } catch (RobotDamagedException ex) {
            return new ResponseDamage(ex.getDamagedBlock());
        }
    }

    public Response processRepair(int blockToRepair) {
        try {
            RobotStatus status = robot.repair(blockToRepair);
            return new ResponseOk(status.getBattery(), status.getX(), status.getY());
        } catch (RobotNoDamageException ex) {
            return new ResponseNoDamage();
        }
    }

    public Response processStep() {
        try {
            RobotStatus status = robot.doStep();
            return new ResponseOk(status.getBattery(), status.getX(), status.getY());
        } catch (RobotCrashedException ex) {
            return new ResponseCrash();
        } catch (RobotBatteryEmptyException ex) {
            return new ResponseBatteryEmpty();
        } catch (RobotCrumbledException ex) {
            return new ResponseCrumbled();
        } catch (RobotDamagedException ex) {
            return new ResponseDamage(ex.getDamagedBlock());
        }
    }

    public Response processTurnLeft() {
        try {
            RobotStatus status = robot.turnLeft();
            return new ResponseOk(status.getBattery(), status.getX(), status.getY());
        } catch (RobotBatteryEmptyException ex) {
            return new ResponseBatteryEmpty();
        }
    }

    public Response processUnknown() {
        return new ResponseUnknownRequest();
    }

    public String getExpectedAddress() {
        return robot.getName();
    }

}
