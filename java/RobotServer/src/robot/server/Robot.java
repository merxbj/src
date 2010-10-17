/*
 * Robot
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
public class Robot {

    private String name;
    private RobotServerInfo info;
    private RobotState currentState;

    public Robot(String name) {
        this.name = name;
        
        /**
         * Generate the robot starting position and direction
         */
        int bat = 100;
        int x = (int) Math.floor(Math.random() * 35) - 17;
        int y = (int) Math.floor(Math.random() * 35) - 17;
        RobotServerInfo.Direction direction = RobotServerInfo.Direction.values()[(int) Math.floor(Math.random() * 4)];
        
        this.info = new RobotServerInfo(bat, x, y, direction);
        this.currentState = new RobotOkState();
    }

    public RobotServerInfo doStep() throws RobotCrashedException, RobotBatteryEmptyException, RobotCrumbledException, RobotDamagedException {
        currentState.doStep(this);
        return info;
    }

    public RobotServerInfo turnLeft() throws RobotBatteryEmptyException {
        currentState.turnLeft(this);
        return info;
    }

    public RobotServerInfo repair(int blockToRepair) throws RobotNoDamageException {
        currentState.repair(this, blockToRepair);
        return info;
    }

    public String pickUp() throws RobotCannotPickUpException {
        return currentState.pickUp(this);
    }

    public RobotServerInfo recharge() throws RobotCrumbledException, RobotDamagedException {
        currentState.recharge(this);
        return info;
    }

    public String getName() {
        return name;
    }

    public String getSecretMessage() {
        return SecretMessageProvider.getRandomSecretMessage();
    }

    public RobotState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RobotState currentState) {
        this.currentState = currentState;
    }

    public RobotServerInfo getInfo() {
        return this.info;
    }

}
