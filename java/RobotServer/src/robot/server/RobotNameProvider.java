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

package robot.server;

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
    public static final Object lock;

    static {
        names = Arrays.asList(new String[] {
            "Jarda","Pepa","Misa","Robert","Karel",
            "Lojza","Vaclav","Tomas","Robocop","Optimus"});
        reservations = new HashMap<String, Boolean>();
        lock = new Object();
    }

    public static String provideName() {
        synchronized (lock) {
            int pick = (int) Math.floor(Math.random() * names.size());
            String name = names.get(pick);
            while (reservations.containsKey(name) && reservations.get(name)) {
                pick = (int) Math.floor(Math.random() * names.size());
                name = names.get(pick);
            }
            reservations.put(name, Boolean.TRUE);
            return name;
        }
    }

    public static void freeName(String name) {
        synchronized (lock) {
            reservations.put(name, Boolean.FALSE);
        }
    }

}
