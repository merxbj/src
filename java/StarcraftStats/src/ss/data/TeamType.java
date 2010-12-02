/*
 * TeamType
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
public enum TeamType {

    ONE_VS_ONE {

        @Override
        public String toString() {
            return "1v1";
        }
        
    }, 
    
    TWO_VS_TWO {

        @Override
        public String toString() {
            return "2v2";
        }

    },

    THREE_VS_THREE {

        @Override
        public String toString() {
            return "3v3";
        }

    },

    FOUR_VS_FOUR {

        @Override
        public String toString() {
            return "4v4";
        }
        
    };

    private static final Map<String, TeamType> stringToTeamType;

    static {
        stringToTeamType = new Hashtable<String, TeamType>();
        stringToTeamType.put("1v1", ONE_VS_ONE);
        stringToTeamType.put("2v2", TWO_VS_TWO);
        stringToTeamType.put("3v3", THREE_VS_THREE);
        stringToTeamType.put("4v4", FOUR_VS_FOUR);
    }

    public static TeamType lookup(String string) {
        return stringToTeamType.get(string);
    }

}
