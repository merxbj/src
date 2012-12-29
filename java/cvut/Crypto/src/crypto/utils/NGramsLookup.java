/*
 * NGramsLookup
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

package crypto.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class NGramsLookup {

    public static List<String> findTrigrams(String sentense) {
        return findNGrams(sentense, 3);
    }
    
    public static List<String> findDigrams(String sentense) {
        return findNGrams(sentense, 2);
    }
    
    private static List<String> findNGrams(String sentense, int size) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < sentense.length() - size; i++) {
            String nGram = sentense.substring(i, i + size);
            int count = 1;
            if (map.containsKey(nGram)) {
                count = map.get(nGram) + 1;
            }
            map.put(nGram, count);
        }
        
        List<String> out = new ArrayList<String>();
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                out.add(entry.getKey());
            }
        }
        return out;
    }

}
