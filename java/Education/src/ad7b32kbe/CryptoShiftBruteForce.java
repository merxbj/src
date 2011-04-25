/*
 * CryptoShiftBruteForce
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

package ad7b32kbe;

import java.util.Arrays;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CryptoShiftBruteForce {

    public static void main(String[] args) {
        
        //String cipherText = args[0].toUpperCase();
        String cipherText = "QYFFNBYPYLSHYRNXUSBYLBOMVUHXFYXBYLNINBYMNLUHAYLIIGUAUCHUHXNBYLYQUMNBYXUSMZIIXUHXUMJCHHCHAQBYYFUHXUALYUNVOHXFYIZZFUR";
        char[] normalizedCipherChars = normalize(cipherText);
        for (byte shift = 1; shift < 26; shift++) {
            char[] openChars = shiftText(normalizedCipherChars, shift);
            String openText = denormalize(openChars);
            System.out.println(openText + " | A = " + shift);
        }
        
    }

    private static char[] shiftText(char[] cipherChars, byte shift) {
        char[] openChars = Arrays.copyOf(cipherChars, cipherChars.length);
        for (int c = 0; c < openChars.length; c++) {
            int ch = openChars[c] - shift;
            ch = (ch >= 0) ? ch : (26 + ch);
            openChars[c] = (char) ch;
        }
        return openChars;
    }

    private static char[] normalize(String denormalizedCipherText) {
        char[] chars = denormalizedCipherText.toCharArray();
        for (int c = 0; c < chars.length; c++) {
            chars[c] -= 65;
            if (chars[c] < 0 || chars[c] > 25) throw new RuntimeException("Unexpected char!");
        }
        return chars;
    }
    
    private static String denormalize(char[] normalizedCipherChars) {
        for (int c = 0; c < normalizedCipherChars.length; c++) {
            normalizedCipherChars[c] += 65;
            if (normalizedCipherChars[c] < 65 || normalizedCipherChars[c] > 90) throw new RuntimeException("Unexpected char!");
        }
        return new String(normalizedCipherChars);
    }
    
}
