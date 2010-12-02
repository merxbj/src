/*
 * Race
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

package ss.data;

import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public enum Race {

    PROTOSS {

        @Override
        public String toString() {
            return "Protoss";
        }

    },

    TERRAN {

        @Override
        public String toString() {
            return "Terran";
        }

    },

    ZERG {

        @Override
        public String toString() {
            return "Zerg";
        }

    },

    RANDOM {

        @Override
        public String toString() {
            return "Random";
        }

    };

    private static final Map<String, Race> stringToRace;

    static {
        stringToRace = new Hashtable<String, Race>();
        stringToRace.put("Protoss", PROTOSS);
        stringToRace.put("Terran", TERRAN);
        stringToRace.put("Zerg", ZERG);
        stringToRace.put("Random", RANDOM);
    }

    public static Race lookup(String string) {
        return stringToRace.get(string);
    }
}
