/*
 * ServerResponseHandler
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

package robot.client;

import robot.common.Battery;
import robot.common.Position;
import robot.common.exception.*;
import robot.common.response.ResponseHandler;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ServerResponseHandler implements ResponseHandler {

    private Robot robot;

    public ServerResponseHandler(Robot robot) {
        this.robot = robot;
    }

    public void handleBatteryEmpty() throws RobotBatteryEmptyException {
        this.robot.getServer().disconnect();
        throw new RobotBatteryEmptyException("The robot run out of battery!");
    }

    public void handleCannotPickUp() throws RobotCannotPickUpException {
        throw new RobotCannotPickUpException("Pick up command issued without robot standing on 0,0!");
    }

    public void handleCrash() throws RobotCrashedException {
        throw new RobotCrashedException("Robot paced out from the field!");
    }

    public void handleCrumbled() throws RobotCrumbledException {
        throw new RobotCrumbledException("The robot attempted to perform such operation which made him crumbled!");
    }

    public void handleDamage(int damagedBlock) throws RobotDamagedException {
        throw new RobotDamagedException("The robot has damaged block!", damagedBlock);
    }

    public void handleIdentification(String address) {
        this.robot.setName(address);
    }

    public void handleNoDamage() throws RobotNoDamageException {
        throw new RobotNoDamageException("Repair command issued on block that has no damage!");
    }

    public void handleOk(int battery, int x, int y) {
        this.robot.setBattery(new Battery(battery));
        this.robot.setPos(new Position(x,y));
    }

    public void handleSuccess(String secretMessage) {
        this.robot.setSecretMessage(secretMessage);
    }

    public void handleUnknownRequest() throws RobotUnknownRequestException {
        throw new RobotUnknownRequestException("Unknown request sent by client! Programmer error?");
    }

}
