/*
 * Vector
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

package cz.ss.core;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Vector {
    public int x;
    public int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector substract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector multiple(int koef) {
        return new Vector(koef * this.x, koef * this.y);
    }

    public Vector abs() {
        return new Vector(Math.abs(x), Math.abs(y));
    }

    public Vector toDirectionVector() {
        int dirX = this.x == 0 ? 0 : this.x / (Math.abs(this.x));
        int dirY = this.y == 0 ? 0 : this.y / (Math.abs(this.y));
        return new Vector(dirX, dirY);
    }

    public Vector inverse() {
        return new Vector(this.x * -1, this.y * -1);
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", this.x, this.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector other = (Vector) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.x;
        hash = 61 * hash + this.y;
        return hash;
    }

}
