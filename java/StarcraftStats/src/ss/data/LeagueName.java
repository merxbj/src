/*
 * LeagueName
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
public enum LeagueName {

    BRONZE {

        @Override
        public String toString() {
            return "Bronze";
        }

    },

    SILVER {

        @Override
        public String toString() {
            return "Silver";
        }

    },

    GOLD {

        @Override
        public String toString() {
            return "Gold";
        }

    },

    PLATINUM {

        @Override
        public String toString() {
            return "Platinum";
        }

    },

    DIAMOND {

        @Override
        public String toString() {
            return "Diamond";
        }

    };

    private static final Map<String, LeagueName> stringToLeagueName;

    static {
        stringToLeagueName = new Hashtable<String, LeagueName>();
        stringToLeagueName.put("Bronze", BRONZE);
        stringToLeagueName.put("Silver", SILVER);
        stringToLeagueName.put("Gold", GOLD);
        stringToLeagueName.put("Platinum", PLATINUM);
        stringToLeagueName.put("Diamond", DIAMOND);
    }

    public static LeagueName lookup(String string) {
        return stringToLeagueName.get(string);
    }

}
