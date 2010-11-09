/*
 * AutomaticRobot
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

import robot.common.RobotInfo;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class AutomaticRobot {

    SmartRobot robot;

    public AutomaticRobot(SmartRobot robot) {
        this.robot = robot;
    }

    public String findSecret() {
        RobotInfo info = robot.initialize();

        int xPos = info.getPosition().x;
        while (xPos != 0) {
            if (xPos > 0) {
                robot.stepLeft();
                xPos--;
            } else {
                robot.stepRight();
                xPos++;
            }
        }

        int yPos = info.getPosition().y;
        while (yPos != 0) {
            if (yPos > 0) {
                robot.stepDown();
                yPos--;
            } else {
                robot.stepUp();
                yPos++;
            }
        }

        return robot.pickUp();
    }

}
