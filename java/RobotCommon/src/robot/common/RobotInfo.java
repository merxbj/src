/*
 * RobotInfo
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

package robot.common;


/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotInfo {
    protected Position position;
    protected Direction direction;

    public RobotInfo() {
        this(100, 0, 0, Direction.Unknown); // a new robot comes fully charged
    }

    public RobotInfo(int battery, int x, int y, Direction direction) {
        this.position = new Position(x, y);
        this.direction = direction;
    }

    public Position getPosition() {
        return this.position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction newDirection) {
        this.direction = newDirection;
    }

}
