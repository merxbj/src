/*
 * DoubleTableCipherKeyGenerator
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
package crypto.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class DoubleTableCipherKeyGenerator implements Iterable<DoubleTableCipherKey> {

    List<DoubleTableCipherKey> keys;
    
    public DoubleTableCipherKeyGenerator(int textLength) {
        generateKeys(textLength);
    }

    @Override
    public Iterator<DoubleTableCipherKey> iterator() {
        return keys.iterator();
    }
    
    public int count() {
        return keys.size();
    }

    private void generateKeys(int length) {
        TableCipherKeyGenerator generator = new TableCipherKeyGenerator(length);
        keys = new LinkedList<DoubleTableCipherKey>();
        
        for (TableCipherKey outerKey : generator) {
            for (int divider = 2; divider < length; divider++) {
                int division = length / divider;
                if ((division > 1) && ((division * divider) <= length)) {
                    keys.add(new DoubleTableCipherKey(outerKey, new TableCipherKey(divider, division)));
                }
            }
        }
    }
    
}
