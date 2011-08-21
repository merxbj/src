/*
 * SolutionTripplet
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
package cluedo.core;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class SolutionTripplet {
    private Suspect suspect;
    private Room room;
    private Weapon weapon;

    public SolutionTripplet(Suspect suspect, Room room, Weapon weapon) {
        this.suspect = suspect;
        this.room = room;
        this.weapon = weapon;
    }

    public Room getRoom() {
        return room;
    }

    public Suspect getSuspect() {
        return suspect;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SolutionTripplet other = (SolutionTripplet) obj;
        if (this.suspect != other.suspect) {
            return false;
        }
        if (this.room != other.room) {
            return false;
        }
        if (this.weapon != other.weapon) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.suspect != null ? this.suspect.hashCode() : 0);
        hash = 11 * hash + (this.room != null ? this.room.hashCode() : 0);
        hash = 11 * hash + (this.weapon != null ? this.weapon.hashCode() : 0);
        return hash;
    }

}
