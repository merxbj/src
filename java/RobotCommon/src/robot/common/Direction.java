/*
 * Direction
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
import java.util.Map.Entry;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public enum Direction {

    North, West, South, East, Unknown;

    protected static final List<Direction> directionRotationOrder;
    protected static final EnumMap<Direction, Vector> dirToVec;
    static {
        directionRotationOrder = Arrays.asList(new Direction[] {Direction.North, Direction.West, Direction.South, Direction.East});
        dirToVec = new EnumMap<Direction, Vector>(Direction.class);
        dirToVec.put(Direction.North,     new Vector( 0, 1));
        dirToVec.put(Direction.East,      new Vector( 1, 0));
        dirToVec.put(Direction.South,     new Vector(-1, 0));
        dirToVec.put(Direction.West,      new Vector( 1, 0));
        dirToVec.put(Direction.Unknown,   new Vector( 0, 0));
    }

    public static Direction getNextDirection(Direction current) {
        int directionIndex = directionRotationOrder.indexOf(current);
        return directionRotationOrder.get((directionIndex + 1) % 4);
    }

    public static Vector toVector(Direction direction) {
        return dirToVec.get(direction);
    }

    public static Direction fromVector(Vector vector) {
        for (Entry<Direction, Vector> e : dirToVec.entrySet()) {
            if (e.getValue().equals(vector)) {
                return e.getKey();
            }
        }
        return null;
    }

}
