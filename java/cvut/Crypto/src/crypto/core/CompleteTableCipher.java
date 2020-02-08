/*
 * CompleteTableCipher
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

import crypto.utils.InvalidKeyException;
import java.util.Iterator;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CompleteTableCipher implements Cipher {

    protected CompleteTable table;

    public CompleteTableCipher() {
        this.table = new CompleteTable();
    }
    
    @Override
    public void assignKey(Key key) {
        if (key instanceof TableCipherKey) {
            TableCipherKey tck = (TableCipherKey) key;
            table.buildTable(tck.width, tck.height);
        }
    }

    @Override
    public char[] cipher(char[] openChars) {
        table.reset();
        if (table.size() < openChars.length) {
            throw new InvalidKeyException("Either invalid key or inappropriate text to cipher provided!");
        }
        for (char ch : openChars) {
            table.encrypt(ch);
        }
        
        table.complete(); // if there are any fields left - they will be stuffed with random characters
        
        char[] cipherChars = new char[table.size()];
        int i = 0;
        Iterator<Character> it = table.columnBasedIterator();
        while (it.hasNext()) {
            cipherChars[i++] = it.next();
        }
        return cipherChars;
    }

    @Override
    public char[] decipher(char[] cipherChars) {
        table.reset();
        if (table.size() < cipherChars.length) {
            throw new InvalidKeyException("Either invalid key or inappropriate text to cipher provided!");
        }
        for (char ch : cipherChars) {
            table.decrypt(ch);
        }
        
        char[] openChars = new char[table.size()];
        int i = 0;
        Iterator<Character> it = table.rowBasedIterator();
        while (it.hasNext()) {
            openChars[i++] = it.next();
        }
        return openChars;
    }
    
}
