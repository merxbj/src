/*
 * PileUnflippedCountComparator
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

package solver.core;

import java.util.Comparator;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class PileUnflippedCountComparator implements Comparator<Pile<Card>> {

    @Override
    public int compare(Pile<Card> o1, Pile<Card> o2) {
        int bottomCount1 = 0;
        for (Pile.Facing facing : o1.getPile().values()) {
            if (facing.equals(Pile.Facing.Bottom)) {
                bottomCount1++;
            }
        }
        
        int bottomCount2 = 0;
        for (Pile.Facing facing : o2.getPile().values()) {
            if (facing.equals(Pile.Facing.Bottom)) {
                bottomCount2++;
            }
        }
        
        return ((Integer) bottomCount1).compareTo(bottomCount2);
    }

}
