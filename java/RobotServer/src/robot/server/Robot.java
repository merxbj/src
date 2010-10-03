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

import robot.server.exception.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Robot {

    private String name;
    private RobotStatus status;
    private RobotState currentState;

    public Robot(String name) {
        this.name = name;
        
        /**
         * Generate the robot starting position and direction
         */
        int bat = 100;
        int x = (int) Math.floor(Math.random() * 35) - 17;
        int y = (int) Math.floor(Math.random() * 35) - 17;
        int dir_x = (int) Math.floor(Math.random() * 3) - 1;
        int dir_y = (dir_x != 0) ? 0 :
            (int) ((Math.random() > 0.5) ? 1 : -1);
        
        this.status = new RobotStatus(bat, x, y, dir_x, dir_y);
        this.currentState = new RobotOkState();
    }

    public RobotStatus doStep() throws RobotCrashedException, RobotBatteryEmptyException, RobotCrumbledException, RobotDamagedException {
        currentState.doStep(this);
        return status;
    }

    public String getName() {
        return name;
    }

    public RobotState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RobotState currentState) {
        this.currentState = currentState;
    }

    public RobotStatus getStatus() {
        return status;
    }

    public void setStatus(RobotStatus status) {
        this.status = status;
    }

}
