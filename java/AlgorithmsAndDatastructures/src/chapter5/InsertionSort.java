/*
 * InsertionSort
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

package chapter5;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class InsertionSort {

    public <T extends Comparable<T>> void sort(T[] a) {
        for (int i = 1; i < a.length; i++) {
            T t = a[i];
            if (t.compareTo(a[0]) == -1) {
                for (int j = i; j > 0; j--) {
                    a[j] = a[j-1];
                }
                a[0] = t;
            } else {
                int j = i;
                while ((j > 0) && (t.compareTo(a[j-1]) == -1)) {
                    a[j] = a[j-1];
                    j--;
                }
                a[j] = t;
            }
        }
    }

}
