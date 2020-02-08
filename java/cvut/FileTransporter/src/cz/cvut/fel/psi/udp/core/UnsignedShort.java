/*
 * UnsignedShort
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
package cz.cvut.fel.psi.udp.core;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class UnsignedShort implements Comparable<UnsignedShort> {

    private short value;

    public UnsignedShort() {
        this((short) 0);
    }

    public UnsignedShort(short number) {
        this.value = number;
    }

    public UnsignedShort(int number) {
        this.value = (short) number;
    }
    
    public UnsignedShort(final UnsignedShort copy) {
        this.value = copy.value;
    }

    public int compareTo(UnsignedShort other) {
        Integer t = this.normalizeToInteger();
        Integer o = other.normalizeToInteger();
        return t.compareTo(o);
    }

    public int normalizeToInteger() {
        return ((int) value) & 0xffff;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UnsignedShort other = (UnsignedShort) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.value;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%d", normalizeToInteger());
    }

    public short getShortValue() {
        return value;
    }

    public UnsignedShort add(UnsignedShort other) {
        return new UnsignedShort(value + other.value);
    }

    public UnsignedShort add(int other) {
        return new UnsignedShort(value + other);
    }

    public UnsignedShort add(short other) {
        return new UnsignedShort(value + other);
    }

    public UnsignedShort substract(UnsignedShort other) {
        return new UnsignedShort(value - other.value);
    }

    public UnsignedShort substract(int other) {
        return new UnsignedShort(value - other);
    }

    public UnsignedShort substract(short other) {
        return new UnsignedShort(value - other);
    }

    public boolean greaterThan(UnsignedShort other) {
        return (this.compareTo(other) == 1);
    }

    public boolean lessThan(UnsignedShort other) {
        return this.compareTo(other) == -1;
    }

    public boolean greaterThanOrEquals(UnsignedShort other) {
        return ((this.compareTo(other) == 1) || (this.compareTo(other) == 0));
    }

    public boolean lessThanOrEquals(UnsignedShort other) {
        return ((this.compareTo(other) == -1) || (this.compareTo(other) == 0));
    }
}
