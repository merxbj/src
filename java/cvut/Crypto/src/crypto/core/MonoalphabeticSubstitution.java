/*
 * MonoalphabeticSubstitution
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
public abstract class MonoalphabeticSubstitution implements Cipher {
    private char[] mapping;
    private char[] inverseMapping;
    protected abstract void createMapping(char[] mapping, char[] inverseMapping, Key key);

    public MonoalphabeticSubstitution() {
        mapping = new char[255];
        inverseMapping = new char[255];
    }
    
    @Override
    public void assignKey(Key key) {
        createMapping(mapping, inverseMapping, key);
    }

    @Override
    public char[] cipher(char[] openChars) {
        char[] cipherChars = Arrays.copyOf(openChars, openChars.length);
        for (int i = 0; i < openChars.length; i++) {
            cipherChars[i] = mapping[openChars[i]];
        }
        return cipherChars;
    }

    @Override
    public char[] decipher(char[] cipherChars) {
        char[] openChars = Arrays.copyOf(cipherChars, cipherChars.length);
        for (int i = 0; i < cipherChars.length; i++) {
            openChars[i] = inverseMapping[cipherChars[i]];
        }
        return openChars;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Mapping: ");
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            out.append(mapping[ch]).append(", ");
        }
        return out.append("\n").toString();
    }

}
