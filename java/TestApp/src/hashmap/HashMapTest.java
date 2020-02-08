/*
 * HashMapTest
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

package hashmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class HashMapTest {

    private static List<Map<Integer, Boolean>> positions = new ArrayList<Map<Integer, Boolean>>(getMaximalniPocetPater()); // hle! pole map!

    public static void main(String[] args) {
        
        for (int i = 0; i < getMaximalniPocetPater(); i++) {
            positions.add(new HashMap<Integer, Boolean>());
        }

        tah(nahodnyInt(), 0); // levely ja osobne cisluju od 0

    }

    private static boolean tah(int sachovnice, int level) {

        if (getMaximalniPocetPater() == level) {
            return false;
        }

        if (nahodnyBoolean()) {
            return true;
        }

        Map<Integer, Boolean> levelPositions = positions.get(level);

        for (int i = 0; i < 50; i++) {
            sachovnice = nahodnyInt();
            Boolean vysledek = levelPositions.get(sachovnice); // nez zacneme zjistovat vysldek, co kdyz uz ho zname?
            if (vysledek == null) {
                vysledek = tah(sachovnice, level + 1); // tak nic, vysledek si musime zjistit
                levelPositions.put(sachovnice, vysledek); // ale do budoucna si ho ulozime
            } else {
                System.out.println("Ty vole, nasel jsem tu pozici v cache!"); // vida! nemusime nic pocitat, vysledek zname
            }

            if (vysledek) {
                return vysledek;
            }

        }

        return false;

    }

    private static int getMaximalniPocetPater() {
        return 10;
    }

    private static boolean nahodnyBoolean() {
        return Math.random() > 0.9 ? true : false;
    }

    private static int nahodnyInt() {
        return (int) Math.ceil(Math.random() * 10);
    }

}
