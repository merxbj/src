/*
 * Faktorizator
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
package cz.merxbj.faktorizace;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Faktorizator {
    public static void main(String[] args) {
        long mod = 1469;
        long max = (long) Math.floor(Math.sqrt(mod));
        for (long l = max; l > 0; l--) {
            //System.out.printf("%d = %d * %d + %d", mod, l, mod / l, mod % l);
            if (((mod % l) == 0)) {
                System.out.printf("%d = %d * %d", mod, l, mod / l);
                if (((l % 2) != 0) && (((mod / l) % 2) != 0)) {
                    System.out.print("possible factor");
                }
                System.out.println("");
            }
        }
    }
}
