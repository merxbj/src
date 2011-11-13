/*
 * RobotServerInfo
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

import robot.common.Direction;
import robot.common.Position;
import robot.common.RobotInfo;
import robot.common.Vector;
import robot.server.exception.RobotOutOfFieldException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotServerInfo extends RobotInfo {

    private int stepsSoFar;

    public static final int MAX_X =  22;
    public static final int MAX_Y =  22;
    public static final int MIN_X = -22;
    public static final int MIN_Y = -22;

    public RobotServerInfo(int x, int y, Direction direction) {
        super(x, y, direction);
        this.stepsSoFar = 0;
    }

    public void move() throws RobotOutOfFieldException {
        Vector vec = Direction.toVector(direction);
        Position pos = getPosition();
        pos.x = pos.x + vec.x;
        pos.y = pos.y + vec.y;
        if (pos.x < MIN_X || pos.x > MAX_X || pos.y < MIN_Y || pos.y > MAX_Y) {
            throw new RobotOutOfFieldException(pos.x, pos.y);
        }
    }

    public void turn() {
        direction = Direction.getNextDirection(direction);
    }

    public int getStepsSoFar() {
        return stepsSoFar;
    }

    public void setStepsSoFar(int stepsSoFar) {
        this.stepsSoFar = stepsSoFar;
    }

}
