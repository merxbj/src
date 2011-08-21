/*
 * GameCard
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
public class GameCard implements Comparable<GameCard> {
    
    private Room room;
    private Suspect suspect;
    private Weapon weapon;

    public GameCard(Room room) {
        this.room = room;
    }

    public GameCard(Suspect suspect) {
        this.suspect = suspect;
    }

    public GameCard(Weapon weapon) {
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
    public int compareTo(GameCard o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameCard other = (GameCard) obj;
        if (this.room != other.room) {
            return false;
        }
        if (this.suspect != other.suspect) {
            return false;
        }
        if (this.weapon != other.weapon) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.room != null ? this.room.hashCode() : 0);
        hash = 97 * hash + (this.suspect != null ? this.suspect.hashCode() : 0);
        hash = 97 * hash + (this.weapon != null ? this.weapon.hashCode() : 0);
        return hash;
    }
    
}
