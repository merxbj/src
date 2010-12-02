/*
 * GameResult
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
public enum GameResult {

    TEAM_A_WON {

        @Override
        public String toString() {
            return "Team A Won";
        }

    },

    TEAM_A_DEFEATED {

        @Override
        public String toString() {
            return "Team A Defeated";
        }

    },

    TIED {

        @Override
        public String toString() {
            return "Teams tied";
        }

    };

    private static final Map<String, GameResult> stringToGameResult;

    static {
        stringToGameResult = new Hashtable<String, GameResult>();
        stringToGameResult.put("TeamADefeated", TEAM_A_DEFEATED);
        stringToGameResult.put("TeamAWon", TEAM_A_WON);
        stringToGameResult.put("Tied", TIED);
    }

    public static GameResult lookup(String string) {
        return stringToGameResult.get(string);
    }
}
