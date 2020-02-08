/*
 * RobotNameProvider
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

package cz.cvut.fel.psi.robot.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RobotNameProvider {

    public static List<String> names;
    public static HashMap<String,Boolean> reservations;

    static {
        names = Arrays.asList(new String[] {
                    "Jardo", "Pepo", "Miso", "Roberte", "Karle",
                    "Lojzo", "Vaclave", "Tomasi", "Robocope", "Optime",
                    "Iron Mane", "Bumblebee", "Martinku", "Chlupatoure Obecny",
                    "Edwarde", "Bello", "Jacobe", "Jaspere"});

        reservations = new HashMap<String, Boolean>();
    }

    public static String provideName() {
        int pick = (int) Math.floor(Math.random() * names.size());
        String name = names.get(pick);
        while (reservations.containsKey(name) && reservations.get(name)) {
            pick = (int) Math.floor(Math.random() * names.size());
            name = names.get(pick);
        }
        reservations.put(name, Boolean.TRUE);
        return name;
    }

    public static void freeName(String name) {
        reservations.put(name, Boolean.FALSE);
    }

}
