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

package cz.cvut.fel.psi.robot.server;

import cz.cvut.fel.psi.robot.common.Direction;
import cz.cvut.fel.psi.robot.common.exception.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Robot implements Comparable<Robot> {

    private String name;
    private RobotServerInfo info;
    private RobotState currentState;

    public Robot(String name) {
        this.name = name;
        
        /**
         * Generate the robot starting position and direction
         * TODO: Move this initialization inside the RobotServerInfo
         */ 
        int x = (int) Math.floor(Math.random() * 43) - (Math.min(Math.abs(RobotServerInfo.MAX_X), Math.abs(RobotServerInfo.MIN_X) - 1));
        int y = (int) Math.floor(Math.random() * 43) - (Math.min(Math.abs(RobotServerInfo.MAX_Y), Math.abs(RobotServerInfo.MIN_Y) - 1));

        Direction direction = Direction.values()[(int) Math.floor(Math.random() * 4)];
        
        this.info = new RobotServerInfo(x, y, direction);
        this.currentState = new RobotOkState();
    }

    public RobotServerInfo doStep() throws RobotCrashedException, RobotCrumbledException, RobotProcessorDamagedException {
        currentState.doStep(this);
        return info;
    }

    public RobotServerInfo turnLeft() {
        currentState.turnLeft(this);
        return info;
    }

    public RobotServerInfo repair(int processorToRepair) throws RobotProcessorOkException {
        currentState.repair(this, processorToRepair);
        return info;
    }

    public String pickUp() throws RobotCannotPickUpException {
        return currentState.pickUp(this);
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

    public int compareTo(Robot t) {
        return this.name.compareTo(t.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Robot other = (Robot) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
