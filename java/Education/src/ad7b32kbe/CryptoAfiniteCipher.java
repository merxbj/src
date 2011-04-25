/*
 * CryptoAfiniteCipher
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
public class CryptoAfiniteCipher {
    
    public static void main(String[] args) {
        String cipherText = "NDUHUCYQIHUSCRUIHKICKDUIKBYPGNUIKBYPGNUIVRHUSUSLUHGXNDUHUIHUVYNXGTUWDYPUKMUGVKNYVGADNGXUIHCYQHDUIRWGPPOYSUYXX";
        //String cipherText = "QYFFNBYPYLSHYRNXUSBYLBOMVUHXFYXBYLNINBYMNLUHAYLIIGUAUCHUHXNBYLYQUMNBYXUSMZIIXUHXUMJCHHCHAQBYYFUHXUALYUNVOHXFYIZZFUR";
        CryptoVocabulary voc = new CryptoVocabulary();
        voc.loadFromFile(args[0]);
        char[] normalizedCipherChars = normalize(cipherText);
        for (byte b = 0; b <= 25; b++) {
            byte[] a = {1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25};
            for (byte i = 0; i < a.length; i++) {
                try {
                    Key key = new Key(a[i], b);
                    char[] openChars = decipher(normalizedCipherChars, key);
                    String openText = denormalize(openChars);
                    if (voc.isSentenseNoSpaces(openText)) {
                        System.out.println(openText + " : " + key);
                    }
                } catch (InvalidKeyException ex) {
                    continue;
                }
            }
        }
    }
    
    private static char[] cipher(char[] openNormalizedChars, Key key) {
        char[] cipherChars = Arrays.copyOf(openNormalizedChars, openNormalizedChars.length);
        for (int i = 0; i < openNormalizedChars.length; i++) {
            cipherChars[i] = (char) ((key.a * cipherChars[i] + key.b) % 25);
        }
        return cipherChars;
    }
    
    private static char[] decipher(char[] cipherNormalizedChars, Key key) throws InvalidKeyException {
        char[] openChars = Arrays.copyOf(cipherNormalizedChars, cipherNormalizedChars.length);
        for (int i = 0; i < cipherNormalizedChars.length; i++) {
            int temp = openChars[i] - key.b;
            temp = (temp >= 0) ? temp : (26 + temp);
            if ((temp % key.a) == 0) {
                openChars[i] = (char) (temp / key.a);
            } else {
                throw new InvalidKeyException("Real value of the decipher alphabet - invalid key.");
            }
        }
        return openChars;
    }
    
    private static char[] normalize(String denormalizedText) {
        char[] chars = denormalizedText.toCharArray();
        for (int c = 0; c < chars.length; c++) {
            chars[c] -= 65;
            if (chars[c] < 0 || chars[c] > 25) throw new RuntimeException("Unexpected char!");
        }
        return chars;
    }
    
    private static String denormalize(char[] normalizedChars) {
        for (int c = 0; c < normalizedChars.length; c++) {
            normalizedChars[c] += 65;
            if (normalizedChars[c] < 65 || normalizedChars[c] > 90) throw new RuntimeException("Unexpected char!");
        }
        return new String(normalizedChars);
    }
    
    private static class Key {
        public byte a, b;

        public Key(byte a, byte b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "Key{" + "a=" + a + ", b=" + b + '}';
        }
    }
    
}
