/*
 * KeyCipher
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

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class KeyCipher extends MonoalphabeticSubstitution {

    @Override
    protected void createMapping(char[] mapping, char[] inverseMapping, Key key) {
        if (key instanceof KeyCipherKey) {
            
            boolean[] seen = new boolean[255]; // to remember which characters have been used with key
            
            // at first map according to the key
            KeyCipherKey kck = (KeyCipherKey) key;
            char mappingIdx = 'A';
            for (int keyChIdx = 0; keyChIdx < kck.key.length; keyChIdx++) {
                mapping[mappingIdx] = kck.key[keyChIdx];
                inverseMapping[kck.key[keyChIdx]] = mappingIdx;
                seen[kck.key[keyChIdx]] = true;
                mappingIdx++;
            }
            
            // map the rest of the alphabet accordingly
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                if (!seen[ch]) {
                    mapping[mappingIdx] = ch;
                    inverseMapping[ch] = mappingIdx;
                    mappingIdx++;
                }
            }
        }
    }
}
