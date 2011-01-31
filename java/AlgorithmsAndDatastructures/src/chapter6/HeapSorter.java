/*
 * HeapSorter
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

package chapter6;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class HeapSorter<K extends Comparable, V> extends BinaryHeap<K, V> {

    public HeapSorter(int maxSize) {
        super(maxSize);
    }

    public <T extends Comparable<T>> void sort(Element<K,V>[] arr) {
        build(arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = deleteMin();
        }
    }
}
