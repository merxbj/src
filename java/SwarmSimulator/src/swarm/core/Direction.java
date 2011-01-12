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

package swarm.core;

import java.util.EnumMap;
import java.util.Map.Entry;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public enum Direction {

    North, West, South, East, Stay, NorthWest, NorthEast, SouthWest, SouthEast, Unknown;
    protected static final EnumMap<Direction, Vector> dirToVec;

    static {
        dirToVec = new EnumMap<Direction, Vector>(Direction.class);
        dirToVec.put(Direction.Stay, new Vector(0, 0));
        dirToVec.put(Direction.North, new Vector(0, -1));
        dirToVec.put(Direction.East, new Vector(1, 0));
        dirToVec.put(Direction.South, new Vector(0, 1));
        dirToVec.put(Direction.West, new Vector(-1, 0));
        dirToVec.put(Direction.NorthWest, new Vector(-1, -1));
        dirToVec.put(Direction.NorthEast, new Vector(1, -1));
        dirToVec.put(Direction.SouthWest, new Vector(-1, 1));
        dirToVec.put(Direction.SouthEast, new Vector(1, 1));
    }

    public Vector toVector() {
        return dirToVec.get(this);
    }

    public static Direction fromVector(Vector vector) {
        for (Entry<Direction, Vector> e : dirToVec.entrySet()) {
            if (e.getValue().equals(vector)) {
                return e.getKey();
            }
        }
        return null;
    }

    public static Direction getRandom() {
        int pick = (int) Math.floor(Math.random() * 9);
        return Direction.values()[pick];
    }
}
