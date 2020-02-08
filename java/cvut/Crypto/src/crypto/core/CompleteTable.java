/*
 * CriptoTable
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

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CompleteTable implements Iterable<Character> {
    
    private static final char[] stuffing = {'X', 'Z', 'J', 'Q'};
    
    private char[][] table;
    private int width;
    private int height;
    private RowBasedAccessor encrypter;
    private ColumnBasedAccessor decrypter;

    public CompleteTable() {
        table = null;
        encrypter = null;
    }
    
    public void buildTable(int x, int y) {
        table = new char[y][x];
        width = x;
        height = y;
        encrypter = new RowBasedAccessor();
        decrypter = new ColumnBasedAccessor();
    }
    
    public void reset() {
        buildTable(width, height); // will rebuild the table, reset accessors
    }
    
    public void encrypt(char ch) {
        if (decrypter != null) {
            decrypter = null; // make sure you will not be able to decrypt any more unless you rebuild
        }
        if (encrypter.hasAccess()) {
            encrypter.setField(ch);
            encrypter.move();
        }
    }
    
    public void decrypt(char ch) {
        if (encrypter != null) {
            encrypter = null; // make sure you will not be able to encrypt any more unless you rebuild
        }
        if (decrypter.hasAccess()) {
            decrypter.setField(ch);
            decrypter.move();
        }
    }

    public Iterator<Character> rowBasedIterator() {
        return new CompleteTableIterator(new RowBasedAccessor());
    }
    
    public Iterator<Character> columnBasedIterator() {
        return new CompleteTableIterator(new ColumnBasedAccessor());
    }
    
    @Override
    public Iterator<Character> iterator() {
        return rowBasedIterator();
    }
    
    public int size() {
        return width * height;
    }

    public void complete() {
        while (encrypter.hasAccess()) {
            encrypter.setField(stuffing[(int) Math.floor(Math.random() * 4)]);
            encrypter.move();
        }
    }
    
    public class CompleteTableIterator implements Iterator<Character> {

        private TableFieldAccessor accessor;

        public CompleteTableIterator(TableFieldAccessor accessor) {
            this.accessor = accessor;
        }
        
        @Override
        public boolean hasNext() {
            return accessor.hasAccess();
        }

        @Override
        public Character next() {
            if (!accessor.hasAccess()) {
                throw new IndexOutOfBoundsException("The table has been squeezed out already!");
            }
            char ch = accessor.getField();
            accessor.move();
            return ch;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet, nor will be ever.");
        }
        
    }
    
    public abstract class TableFieldAccessor {
        protected int x;
        protected int y;

        public TableFieldAccessor() {
            this.x = 0;
            this.y = 0;
        }

        public abstract void move();

        public boolean hasAccess() {
            return ((y < height) && (x < width));
        }
        
        public void setField(char value) {
            table[y][x] = value;
        }
        
        public char getField() {
            return table[y][x];
        }
    }
    
    public class RowBasedAccessor extends TableFieldAccessor {

        @Override
        public void move() {
            if (!hasAccess()) {
                throw new IndexOutOfBoundsException("The table has been squeezed out already!");
            }
            
            if (++x == width) {
                x = 0;
                y++;
            }
        }
        
    }
    
    private class ColumnBasedAccessor extends TableFieldAccessor {

        @Override
        public void move() {
            if (!hasAccess()) {
                throw new IndexOutOfBoundsException("The table has been squeezed out already!");
            }
            
            if (++y == height) {
                y = 0;
                x++;
            }
        }
    }
    
}
