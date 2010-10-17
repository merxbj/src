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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotInfo {
    protected Battery battery;
    protected Position position;
    protected Direction direction;

    protected static final EnumMap<Direction, Vector> directions;
    protected static final List<Direction> directionRotationOrder;
    static {
        directions = new EnumMap<Direction, Vector>(Direction.class);
        directions.put(Direction.North, new Vector( 0, 1));
        directions.put(Direction.East,  new Vector( 1, 0));
        directions.put(Direction.South, new Vector(-1, 0));
        directions.put(Direction.West,  new Vector( 1, 0));

        directionRotationOrder = Arrays.asList(new Direction[] {Direction.North, Direction.West, Direction.South, Direction.East});
    }

    public RobotInfo(int battery, int x, int y, Direction direction) {
        this.battery.level = battery;
        this.position.x = x;
        this.position.y = y;
        this.direction = direction;
    }

    public Battery getBattery() {
        return this.battery;
    }

    public Position getPosition() {
        return this.position;
    }

    protected static class Vector {

        int x;
        int y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static enum Direction {
        North, West, South, East;
    }

}
