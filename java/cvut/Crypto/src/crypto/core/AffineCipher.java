/*
 * AffineCipher
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
public class AffineCipher extends MonoalphabeticSubstitution {

    @Override
    protected void createMapping(char[] mapping, char[] inverseMapping, Key key) {
        if (key instanceof AffineCipherKey) {
            AffineCipherKey ack = (AffineCipherKey) key;
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                char character = (char) (ch - 'A');
                char mapped = (char) ((ack.a * character + ack.b) % 26);
                mapping[ch] = (char) (character + 'A');
                inverseMapping[mapped + 'A'] = ch;
            }
        }
    }
    
}
