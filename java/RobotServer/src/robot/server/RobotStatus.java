/*
 * RobotStatus
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

import robot.server.exception.RobotOutOfFieldException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotStatus {
    private int battery;
    private int x;
    private int y;
    private int dir_x;
    private int dir_y;
    private int stepsSoFar;

    private static final int MAX_X =  17;
    private static final int MAX_Y =  17;
    private static final int MIN_X = -17;
    private static final int MIN_Y = -17;

    public RobotStatus(int battery, int x, int y, int dir_x, int dir_y) {
        this.battery = battery;
        this.x = x;
        this.y = y;
        this.dir_x = dir_x;
        this.dir_y = dir_y;
        this.stepsSoFar = 0;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) throws RobotOutOfFieldException {
        if (x < MIN_X || x > MAX_X) {
            throw new RobotOutOfFieldException();
        }
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) throws RobotOutOfFieldException {
        if (y < MIN_Y || y > MAX_Y) {
            throw new RobotOutOfFieldException();
        }
        this.y = y;
    }

    public int getDir_x() {
        return dir_x;
    }

    public void setDir_x(int dir_x) {
        this.dir_x = dir_x;
    }

    public int getDir_y() {
        return dir_y;
    }

    public void setDir_y(int dir_y) {
        this.dir_y = dir_y;
    }

    public int getStepsSoFar() {
        return stepsSoFar;
    }

    public void setStepsSoFar(int stepsSoFar) {
        this.stepsSoFar = stepsSoFar;
    }

}
