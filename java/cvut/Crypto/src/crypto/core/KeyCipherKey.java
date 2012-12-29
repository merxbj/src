/*
 * SimpleKey
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

import java.util.Arrays;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class KeyCipherKey extends Key {
    public char[] key;

    public KeyCipherKey(String keyWithRepetitions) {
        key = new char[keyWithRepetitions.length()];
        int keyIndex = 0;
        boolean[] seen = new boolean[255];
        char[] keyChars = keyWithRepetitions.toCharArray();
        for (int i = 0; i < keyWithRepetitions.length(); i++) {
            if (!seen[keyChars[i]]) {
                seen[keyChars[i]] = true;
                key[keyIndex++] = keyChars[i];
            }
        }
        key = Arrays.copyOf(key, keyIndex);
    }

    @Override
    public String toString() {
        return new String(key);
    }
}
